package com.cosek.edms.files;

import com.cosek.edms.filecategory.FileCategoryService;
import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.files.Models.BulkFileUploadRequest;
import com.cosek.edms.files.Models.FileRequest;
import com.cosek.edms.folders.FoldersService;
import com.cosek.edms.locations.StorageLocation;
import com.cosek.edms.requests.Requests;
import com.cosek.edms.requests.RequestsRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;
    private final FilesRepository filesRepository;

    @Autowired
    private FileCategoryService fileCategoryService;

    @Autowired
    private final FoldersService foldersService;

    @Autowired
    private final RequestsRepository requestsRepository;

    @Autowired
    private final UserRepository userRepository;


    @GetMapping("/{id}")
    public ResponseEntity<Files> getFileById(@PathVariable Long id) {
        Optional<Files> file = filesService.getFileById(id);
        return file.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllFiles(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) boolean isSuperAdmin) {

        System.out.println("isSuperAdmin: " + isSuperAdmin);
        System.out.println("organizationId: " + organizationId);

        Map<String, Object> response = new HashMap<>();
        List<Files> files;

        if (isSuperAdmin) {
            files = filesRepository.findAll(); // ✅ Super Admin gets all files

            // ✅ Fetch only rackName, shelfName, and archivalBoxName for super admin
            List<Object[]> allArchivalBoxes = filesRepository.findAllArchivalBoxesWithHierarchy();
            List<Map<String, Object>> formattedBoxes = formatFilteredArchivalBoxes(allArchivalBoxes);
            response.put("archivalBoxes", formattedBoxes);
        } else if (organizationId != null) {
            files = filesRepository.findByOrganizationId(organizationId); // ✅ Org users see only their files

            // ✅ Fetch filtered archival boxes for organization
            List<Object[]> archivalBoxes = filesRepository.findArchivalBoxesWithHierarchyByOrganization(organizationId);
            List<Map<String, Object>> formattedBoxes = formatFilteredArchivalBoxes(archivalBoxes);
            response.put("archivalBoxes", formattedBoxes);
        } else {
            files = new ArrayList<>(); // ✅ Return empty if no filtering criteria
            response.put("archivalBoxes", new ArrayList<>());
        }

        response.put("files", files);

        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> formatFilteredArchivalBoxes(List<Object[]> boxes) {
        return boxes.stream()
                .map(box -> {
                    Map<String, Object> boxMap = new HashMap<>();
                    boxMap.put("rackName", box[0]);        // ✅ Rack Name
                    boxMap.put("shelfName", box[1]);       // ✅ Shelf Name
                    boxMap.put("archivalBoxName", box[2]); // ✅ Archival Box Name
                    return boxMap;
                })
                .collect(Collectors.toList());
    }


    @PostMapping("/{fileId}/check-in")
    public ResponseEntity<String> checkFileIn(@PathVariable Long fileId) {
        User currentUser = filesService.getLoggedInUser();

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        try {
            String message = filesService.checkFileIn(fileId);
            return ResponseEntity.ok(message);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    @PostMapping("/{fileId}/check-out")
    public ResponseEntity<String> checkFileOut(@PathVariable Long fileId) {
        try {
            String message = filesService.checkFileOut(fileId);
            return ResponseEntity.ok(message);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    @GetMapping("/requests")
    public ResponseEntity<List<Map<String, Object>>> getRequests(
            @RequestParam(required = false) boolean isAdmin,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Integer boxNumber
    ) {
        User currentUser = filesService.getLoggedInUser();
        List<Requests> requests;

        if (isAdmin) {
            if (organizationId != null) {
                requests = requestsRepository.findByOrganizationId(organizationId);
            } else if (boxNumber != null) {
                requests = requestsRepository.findByBoxNumber(boxNumber);
            } else {
                requests = requestsRepository.findAll();
            }
        } else {
            requests = requestsRepository.findByUserId(currentUser.getId());
        }

        // Convert requests to enhanced response with additional details
        List<Map<String, Object>> enhancedRequests = requests.stream()
                .map(request -> {
                    Map<String, Object> enhancedRequest = new HashMap<>();

                    // Basic request details
                    enhancedRequest.put("id", request.getId());
                    enhancedRequest.put("requestType", request.getRequestType());
                    enhancedRequest.put("status", request.getStatus());
                    enhancedRequest.put("requestDate", request.getRequestDate());
                    enhancedRequest.put("completedDate", request.getCompletedDate());
                    enhancedRequest.put("boxNumber", request.getBoxNumber());
                    enhancedRequest.put("organization", request.getOrganization());

                    // File details
                    Files file = request.getFile();
                    if (file != null) {
                        Map<String, Object> fileDetails = new HashMap<>();
                        fileDetails.put("id", file.getId());

                        // ✅ Fixed: Prevent NullPointerException when responsibleUser is null
                        User responsibleUser = file.getResponsibleUser();
                        String responsibleUserName = (responsibleUser != null)
                                ? responsibleUser.getFirst_name() + " " + responsibleUser.getLast_name()
                                : "N/A"; // Default name when user is null
                        fileDetails.put("name", responsibleUserName);

                        fileDetails.put("boxNumber", file.getBoxNumber());

                        // Add archival box details
                        StorageLocation archivalBox = file.getArchivalBox();
                        if (archivalBox != null) {
                            Map<String, Object> archivalBoxDetails = new HashMap<>();
                            archivalBoxDetails.put("id", archivalBox.getId());
                            archivalBoxDetails.put("name", archivalBox.getName());

                            // Add shelf details
                            StorageLocation shelf = archivalBox.getParent();
                            if (shelf != null) {
                                Map<String, Object> shelfDetails = new HashMap<>();
                                shelfDetails.put("id", shelf.getId());
                                shelfDetails.put("name", shelf.getName());

                                // Add rack details
                                StorageLocation rack = shelf.getParent();
                                if (rack != null) {
                                    Map<String, Object> rackDetails = new HashMap<>();
                                    rackDetails.put("id", rack.getId());
                                    rackDetails.put("name", rack.getName());
                                    shelfDetails.put("rack", rackDetails);
                                }

                                archivalBoxDetails.put("shelf", shelfDetails);
                            }

                            fileDetails.put("archivalBox", archivalBoxDetails);
                        }

                        enhancedRequest.put("file", fileDetails);
                    } else {
                        // If file is null, set fileId from request
                        Map<String, Object> fileDetails = new HashMap<>();
                        fileDetails.put("id", request.getFileId());
                        fileDetails.put("name", "File " + request.getFileId());
                        enhancedRequest.put("file", fileDetails);
                    }

                    // User details
                    User user = request.getUser();
                    if (user != null) {
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("id", user.getId());
                        userDetails.put("first_name", user.getFirst_name());
                        userDetails.put("last_name", user.getLast_name());
                        userDetails.put("email", user.getEmail());
                        enhancedRequest.put("user", userDetails);
                    } else {
                        // Try to find user who checked out file
                        Long checkedOutBy = file != null ? file.getCheckedOutBy() : null;
                        if (checkedOutBy != null) {
                            Optional<User> fileUser = userRepository.findById(checkedOutBy);
                            if (fileUser.isPresent()) {
                                Map<String, Object> userDetails = new HashMap<>();
                                userDetails.put("id", fileUser.get().getId());
                                userDetails.put("first_name", fileUser.get().getFirst_name());
                                userDetails.put("last_name", fileUser.get().getLast_name());
                                userDetails.put("email", fileUser.get().getEmail());
                                enhancedRequest.put("user", userDetails);
                            }
                        }
                    }

                    return enhancedRequest;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(enhancedRequests);
    }



    @PostMapping("/{requestId}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long requestId) {
        try {
            String message = filesService.approveRequest(requestId);
            return ResponseEntity.ok(message);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("all/{id}")
    public ResponseEntity<List<Files>> getAllFilesById(@PathVariable Long id) {
        List<Files> files = filesService.getFilesByCreator(id);
        return ResponseEntity.ok(files);
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<Files> updateFile(@PathVariable Long id, @RequestBody Files updatedFile) {
//        Files file = filesService.updateFile(id, updatedFile);
//        return ResponseEntity.ok(file);
//    }

    @PutMapping("/update-multiple")
    public ResponseEntity<List<Files>> updateMultipleFiles(@RequestBody List<Files> files) {
        List<Files> updatedFiles = filesService.updateMultipleFiles(files);
        return ResponseEntity.ok(updatedFiles);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        filesService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleFiles(@RequestBody List<Long> ids) {
        filesService.deleteMultipleFiles(ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fileId}/assign-case-study/{fileCategoryId}")
    public ResponseEntity<Files> assignFileToFileCategory(
            @PathVariable Long fileId,
            @PathVariable Long fileCategoryId) {

        Files updatedFile = filesService.assignFileToFileCategory(fileId, fileCategoryId);
        return ResponseEntity.ok(updatedFile);
    }

    @PutMapping("/{fileId}/assign-folder/{folderId}")
    public ResponseEntity<Files> assignFileToFolder(
            @PathVariable Long fileId,
            @PathVariable Long folderId) {

        Files updatedFile = filesService.assignFileToFolder(fileId, folderId);
        return ResponseEntity.ok(updatedFile);
    }

    @PostMapping("/add")
    public ResponseEntity<Files> addFile(@RequestBody FileRequest fileRequest) {
        Files savedFile = filesService.addFile(fileRequest);
        return ResponseEntity.ok(savedFile);
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<?> bulkUpload(@RequestBody BulkFileUploadRequest bulkRequest) {
        try {
            List<Files> uploadedFiles = filesService.bulkUpload(bulkRequest);
            return ResponseEntity.ok(uploadedFiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files.");
        }
    }


}
