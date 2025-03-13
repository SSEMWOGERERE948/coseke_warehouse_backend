package com.cosek.edms.files;

import com.cosek.edms.MailingService.MailingDetails;
import com.cosek.edms.MailingService.MailingServiceService;
import com.cosek.edms.filecategory.FileCategory;
import com.cosek.edms.filecategory.FileCategoryRepository;
import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.files.Models.BulkFileUploadRequest;
import com.cosek.edms.files.Models.FileRequest;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.folders.FoldersRepository;
import com.cosek.edms.locations.StorageLocation;
import com.cosek.edms.locations.StorageLocationRepository;
import com.cosek.edms.organisation.Organization;
import com.cosek.edms.organisation.OrganizationRepository;
import com.cosek.edms.requests.Requests;
import com.cosek.edms.requests.RequestsRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final UserRepository userRepository;
    private final FileCategoryRepository fileCategoryRepository;
    private final FoldersRepository foldersRepository;
    private final RequestsRepository requestsRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final OrganizationRepository organizationRepository;

    private final FilesRepository filesRepository;

    @Autowired
    private MailingServiceService mailingService;

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username).orElseThrow(() ->
                new IllegalArgumentException("User not found")
        );
    }

    public Files addFile(Files file) {
        if (file.getFileCategory() != null && file.getFileCategory().getId() != null) {
            FileCategory fileCategory = fileCategoryRepository.findById(file.getFileCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("FileCategory not found"));
            file.setFileCategory(fileCategory);
        }

        if (file.getFolder() != null && file.getFolder().getId() != null) {
            Folders folder = foldersRepository.findById(file.getFolder().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
            file.setFolder(folder);
        }

        User loggedInUser = getLoggedInUser();
        file.setResponsibleUser(loggedInUser);

        return filesRepository.save(file);
    }
    public Optional<Files> getFileById(Long id) {
        return filesRepository.findById(id);
    }

        public List<Files> getAllFiles(Long organizationId, boolean isSuperAdmin) {
            System.out.println("isSuperAdmin: " + isSuperAdmin); // ✅ Debug Log
            System.out.println("organizationId: " + organizationId);

            if (isSuperAdmin) {
                return filesRepository.findAll(); // ✅ SUPER_ADMIN gets all files
            }

            if (organizationId != null) {
                return filesRepository.findByOrganizationId(organizationId); // ✅ Other users see only their org
            }

            return new ArrayList<>(); // ✅ Fallback (should never happen)
        }



    // Fetch files by the creator (for ADMIN and USER roles)
    public List<Files> getFilesByCreator(Long id) {
        return filesRepository.findByCreatedBy(id);
    }

//    public Files updateFile(Long id, Files updatedFile) {
//        User loggedInUser = getLoggedInUser();
//        return filesRepository.findById(id)
//                .map(file -> {
//                    file.setPID(updatedFile.getPID());
//                    file.setBoxNumber(updatedFile.getBoxNumber());
//                    file.setFolder(updatedFile.getFolder());
//                    file.setResponsibleUser(loggedInUser);
//                    return filesRepository.save(file);
//                })
//                .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));
//    }

    public List<Files> updateMultipleFiles(List<Files> files) {
        User loggedInUser = getLoggedInUser();
        files.forEach(file -> file.setResponsibleUser(loggedInUser));
        return filesRepository.saveAll(files);
    }

    public void deleteFile(Long id) {
        filesRepository.deleteById(id);
    }

    public void deleteMultipleFiles(List<Long> ids) {
        filesRepository.deleteAllById(ids);
    }

    public Files assignFileToFileCategory(Long fileId, Long fileCategoryId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        FileCategory fileCategory = fileCategoryRepository.findById(fileCategoryId)
                .orElseThrow(() -> new RuntimeException("Case study not found"));

        file.setFileCategory(fileCategory);
        return filesRepository.save(file);
    }

    public Files assignFileToFolder(Long fileId, Long folderId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Folders folder = foldersRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        file.setFolder(folder);
        return filesRepository.save(file);
    }

    @Transactional
    public String checkFileOut(Long fileId) throws AccessDeniedException {
        // Use a synchronized block for this specific file ID to prevent concurrent requests
        synchronized (String.valueOf(fileId).intern()) {
            // Check if there's already a pending request for this file by this user
            User currentUser = getLoggedInUser();
            boolean existingRequest = requestsRepository.existsByFileIdAndUserIdAndStatus(
                    fileId, currentUser.getId(), "Requested");

            if (existingRequest) {
                return "You have already requested this file. Please wait for approval.";
            }

            Files file = filesRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found."));

            if (!"Available".equals(file.getStatus())) {
                throw new IllegalStateException("File is not available for checkout.");
            }

            boolean isSuperAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));

            if (!isSuperAdmin) {
                if (file.getOrganization() == null ||
                        !file.getOrganization().getId().equals(currentUser.getOrganization().getId())) {
                    throw new AccessDeniedException("You can only request files within your organization.");
                }
            }

            // Change status to "Requested"
            file.setStatus("Requested");
            file.setCheckedOutBy(currentUser.getId());
            filesRepository.save(file);

            // Create a "Requested" request with fileId included
            Requests request = Requests.builder()
                    .file(file)
                    .fileId(file.getId())
                    .user(currentUser)
                    .requestType("Request File")
                    .status("Requested")
                    .requestDate(LocalDateTime.now())
                    .boxNumber(file.getBoxNumber())
                    .organization(file.getOrganization())
                    .build();

            requestsRepository.save(request);

            // Send Email Notification
            sendCheckOutNotification(file, currentUser);

            return "File checkout requested and pending approval.";
        }
    }
    private void sendCheckOutNotification(Files file, User currentUser) {
        String recipientEmail = "strevor948@gmail.com"; // New recipient email
        String subject = "File Checkout Request - Pending Approval";
        String messageBody = String.format(
                "Dear Admin,\n\nUser %s (Email: %s) has requested to check out a file.\n\nFile Details:\n" +
                        "- File ID: %d\n- Box Number: %d\n- Organization: %s\n\nPlease log in to approve or reject the request.\n\nBest Regards,\nEDMS System",
                currentUser.getFirst_name() + " " + currentUser.getLast_name(),
                currentUser.getEmail(),
                file.getId(),
                file.getBoxNumber(),
                file.getOrganization().getName()
        );

        MailingDetails mailDetails = MailingDetails.builder()
                .recipient(new String[]{recipientEmail}) // ✅ Only sending to strevor948@gmail.com
                .msgBody(messageBody)
                .subject(subject)
                .build();

        mailingService.sendMail(mailDetails, "bob.wabusa@coseke.com");
    }



    @Transactional
    public String checkFileIn(Long requestId) throws AccessDeniedException {
        // ✅ Fetch the request by requestId (not fileId)
        Requests request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found."));

        // ✅ Ensure request is "Approved"
        if (!"Approved".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Only approved files can be checked in.");
        }

        // ✅ Fetch the correct file using request.getFileId()
        Files file = filesRepository.findById(request.getFile().getId())
                .orElseThrow(() -> new ResourceNotFoundException("File not found."));

        User currentUser = getLoggedInUser();
        boolean isSuperAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));

        // ✅ Ensure only the user who checked out the file or an admin can check it in
        if (!isSuperAdmin && !Objects.equals(file.getCheckedOutBy(), currentUser.getId())) {
            throw new AccessDeniedException("Only the user who checked out the file or an admin can check it in.");
        }

        // ✅ Change file status back to "Available"
        file.setStatus("Available");
        file.setCheckedOutBy(null);
        filesRepository.save(file);

        // ✅ Mark request as "Completed"
        request.setStatus("Completed");
        request.setCompletedDate(LocalDateTime.now());
        requestsRepository.save(request);

        return "File checked in successfully.";
    }



    @Transactional
    public String approveRequest(Long requestId) throws AccessDeniedException {
        Requests request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found."));

        if (!"Requested".equals(request.getStatus())) {
            throw new IllegalStateException("Only pending requests can be approved.");
        }

        User currentUser = getLoggedInUser();
        boolean isSuperAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));

        if (!isSuperAdmin) {
            throw new AccessDeniedException("Only an admin can approve requests.");
        }

        // ✅ Update request status to "Approved"
        request.setStatus("Approved");
        request.setCompletedDate(LocalDateTime.now());
        requestsRepository.save(request);

        // ✅ Change file status to "Unavailable"
        Files file = filesRepository.findById(request.getFile().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Associated file not found."));

        file.setStatus("Unavailable");
        file.setCheckedOutBy(request.getUser().getId()); // Track who checked it out
        filesRepository.save(file);

        return "Request approved. File is now checked out.";
    }



    public Files addFile(FileRequest request) {
        StorageLocation archivalBox = storageLocationRepository.findById(request.getArchivalBoxId())
                .orElseThrow(() -> new ResourceNotFoundException("Archival Box not found"));

        Folders folder = foldersRepository.findById(request.getFolderId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        Files file = Files.builder()
                .boxNumber(request.getBoxNumber())
                .archivalBox(archivalBox)
                .folder(folder)
                .status("Available")
                .build();

        return filesRepository.save(file);
    }
    public List<Files> bulkUpload(BulkFileUploadRequest bulkRequest) {
        StorageLocation archivalBox = storageLocationRepository.findById(bulkRequest.getArchivalBoxId())
                .orElseThrow(() -> new ResourceNotFoundException("Archival Box not found"));

        Organization organization = organizationRepository.findById(bulkRequest.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        List<Files> newFiles = new ArrayList<>();

        if (bulkRequest.getMetadataJson() != null && !bulkRequest.getMetadataJson().isEmpty()) {
            for (Map<String, Object> fileMetadata : bulkRequest.getMetadataJson()) {
                Files file = Files.builder()
                        .boxNumber(bulkRequest.getBoxNumber())
                        .archivalBox(archivalBox)
                        .organization(organization)
                        .status("Available") // ✅ Ensure files are created as Available
                        .build();

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    file.setMetadataJson(mapper.writeValueAsString(fileMetadata));
                } catch (Exception e) {
                    System.err.println("Error serializing metadata: " + e.getMessage());
                }

                newFiles.add(file);
            }
        }

        return filesRepository.saveAll(newFiles);
    }



}
