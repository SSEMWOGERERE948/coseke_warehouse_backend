package com.cosek.edms.folders;

import com.cosek.edms.exception.ResourceNotFoundException;
import com.cosek.edms.folders.Models.AssignFolderToDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/folders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoldersController {

    private final FoldersService foldersService;

    @PostMapping("/add")
    public ResponseEntity<Folders> addFolder(@RequestBody Folders folder) {
        Folders createdFolder = foldersService.addFolder(folder);
        return ResponseEntity.ok(createdFolder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Folders> getFolderById(@PathVariable Long id) {
        Optional<Folders> folder = foldersService.getFolderById(id);
        return folder.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Folders>> getAllFolders() {
        List<Folders> folders = foldersService.getAllFolders();
        return ResponseEntity.ok(folders);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Folders> updateFolder(@PathVariable Long id, @RequestBody Folders updatedFolder) {
        Folders folder = foldersService.updateFolder(id, updatedFolder);
        return ResponseEntity.ok(folder);
    }

    @PutMapping("/update-multiple")
    public ResponseEntity<List<Folders>> updateMultipleFolders(@RequestBody List<Folders> folders) {
        List<Folders> updatedFolders = foldersService.updateMultipleFolders(folders);
        return ResponseEntity.ok(updatedFolders);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        foldersService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultipleFolders(@RequestBody List<Long> ids) {
        foldersService.deleteMultipleFolders(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/manage-assignment")
    public ResponseEntity<String> manageFolderAssignment(@RequestBody AssignFolderToDepartment request) {
        try {
            if ("ASSIGN".equals(request.getOperation())) {
                foldersService.assignFoldersToDepartment(request.getDepartmentId(), request.getFolderIds());
                return ResponseEntity.ok("Folders assigned successfully.");
            } else if ("UNASSIGN".equals(request.getOperation())) {
                foldersService.unassignFoldersFromDepartment(request.getDepartmentId(), request.getFolderIds());
                return ResponseEntity.ok("Folders unassigned successfully.");
            } else {
                return ResponseEntity.badRequest().body("Invalid operation specified.");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("An error occurred while managing folder assignment: " + e.getMessage());
        }
    }

    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<List<Folders>> getAssignedFolders(@PathVariable Long departmentId) {
        try {
            List<Folders> assignedFolders = foldersService.getAssignedFolders(departmentId);
            return ResponseEntity.ok(assignedFolders);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Folders>> getFoldersByUserDepartments(@PathVariable Long userId) {
        List<Folders> folders = foldersService.getfoldersByUserId(userId);
        return ResponseEntity.ok(folders);
    }
}

