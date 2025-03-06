package com.cosek.edms.files;

import com.cosek.edms.filecategory.FileCategoryService;
import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.files.Models.BulkFileUploadRequest;
import com.cosek.edms.files.Models.FileRequest;
import com.cosek.edms.folders.FoldersService;
import com.cosek.edms.requests.Requests;
import com.cosek.edms.requests.RequestsRepository;
import com.cosek.edms.user.User;
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
    public ResponseEntity<List<Requests>> getRequests(
            @RequestParam(required = false) boolean isAdmin,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Integer boxNumber
    ) {
        User currentUser = filesService.getLoggedInUser(); // ✅ Get the logged-in user

        List<Requests> requests;

        if (isAdmin) {
            if (organizationId != null) {
                requests = requestsRepository.findByOrganizationId(organizationId); // ✅ Filter by Organization
            } else if (boxNumber != null) {
                requests = requestsRepository.findByBoxNumber(boxNumber); // ✅ Filter by Box Number
            } else {
                requests = requestsRepository.findAll(); // ✅ Admin sees all requests
            }
        } else {
            requests = requestsRepository.findByUserId(currentUser.getId()); // ✅ Users see their own requests
        }

        return ResponseEntity.ok(requests);
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
