package com.cosek.edms.files;

import com.cosek.edms.casestudy.CaseStudy;
import com.cosek.edms.casestudy.CaseStudyRepository;
import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.folders.FoldersRepository;
import com.cosek.edms.requests.Requests;
import com.cosek.edms.requests.RequestsRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import com.cosek.edms.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;
    private final UserRepository userRepository;
    private final CaseStudyRepository caseStudyRepository;
    private final FoldersRepository foldersRepository;
    private final RequestsRepository requestsRepository;


    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username).orElseThrow(() ->
                new IllegalArgumentException("User not found")
        );
    }

    public Files addFile(Files file) {
        if (file.getCaseStudy() != null && file.getCaseStudy().getId() != null) {
            CaseStudy caseStudy = caseStudyRepository.findById(file.getCaseStudy().getId())
                    .orElseThrow(() -> new EntityNotFoundException("CaseStudy not found"));
            file.setCaseStudy(caseStudy);
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

    public List<Files> getAllFiles() {
        return filesRepository.findAll();
    }

    // Fetch files by the creator (for ADMIN and USER roles)
    public List<Files> getFilesByCreator(Long id) {
        return filesRepository.findByCreatedBy(id);
    }

    public Files updateFile(Long id, Files updatedFile) {
        User loggedInUser = getLoggedInUser();
        return filesRepository.findById(id)
                .map(file -> {
                    file.setPID(updatedFile.getPID());
                    file.setBoxNumber(updatedFile.getBoxNumber());
                    file.setFolder(updatedFile.getFolder());
                    file.setResponsibleUser(loggedInUser);
                    return filesRepository.save(file);
                })
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));
    }

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

    public Files assignFileToCaseStudy(Long fileId, Long caseStudyId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        CaseStudy caseStudy = caseStudyRepository.findById(caseStudyId)
                .orElseThrow(() -> new RuntimeException("Case study not found"));

        file.setCaseStudy(caseStudy);
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


    public String checkFileIn(Long fileId) throws AccessDeniedException {
        // Fetch the file by its ID
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found."));

        // Fetch the current logged-in user
        User currentUser = getLoggedInUser();

        // Find all requests for this file
        List<Requests> requests = requestsRepository.findByFilesAndUser(file, file.getResponsibleUser());

        // Find active checkout requests (not returned)
        List<Requests> activeRequests = requests.stream()
                .filter(r -> Objects.equals(r.getFiles().getId(), fileId) &&
                        !Objects.equals(r.getStage(), "Returned") &&
                        Objects.equals(r.getStage(), "Approved"))
                .toList();

        // Check if file is actually checked out
        if (activeRequests.isEmpty()) {
            throw new IllegalStateException("This file has not been checked out.");
        }

        // Verify the current user is the one who checked it out
        boolean isCheckedOutByCurrentUser = activeRequests.stream()
                .anyMatch(r -> r.getUser().equals(currentUser));

        if (!isCheckedOutByCurrentUser) {
            throw new AccessDeniedException("Only the user who checked out the file can check it in.");
        }

        // Update all active requests to Returned status
        activeRequests.forEach(request -> {
            request.setStage("Returned");
            requestsRepository.save(request);
        });

        // Update file status
        file.setStatus("Available");
        filesRepository.save(file);

        return "File checked in successfully.";
    }
}
