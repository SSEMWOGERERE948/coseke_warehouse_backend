package com.cosek.edms.filecategory;

import com.cosek.edms.filecategory.Modals.AssignUserRequest;
import com.cosek.edms.filecategory.Modals.FileCategoryAssigned;
import com.cosek.edms.filecategory.Modals.FileCategoryRequest;
import com.cosek.edms.filecategory.Modals.UnassignRequest;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleService;
import com.cosek.edms.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/file-categories")
@CrossOrigin(origins = "*")
public class FileCategoryController {

    @Autowired
    private FileCategoryService fileCategoryService;

    @Autowired
    private RoleService roleService;

    // Get all case studies
    @GetMapping("/all")
    public List<FileCategory> getAllCaseStudies() {
        return fileCategoryService.getAllFileCategories();
    }

    // Get a case study by ID
    @GetMapping("/{id}")
    public ResponseEntity<FileCategory> getFileCategoryById(@PathVariable Long id) {
        Optional<FileCategory> fileCategory = fileCategoryService.getFileCategoryById(id);
        return fileCategory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing case study
    @PutMapping("/update/{id}")
    public ResponseEntity<FileCategory> updateFileCategory(@PathVariable Long id, @RequestBody FileCategory updatedFileCategory) {
        try {
            FileCategory fileCategory = fileCategoryService.updateFileCategory(id, updatedFileCategory);
            return ResponseEntity.ok(fileCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a case study
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFileCategory(@PathVariable Long id) {
        fileCategoryService.deleteFileCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-cases")
    public ResponseEntity<?> createFileCategory(@RequestBody FileCategoryRequest request) {
        try {
            // Create the case study without any role assignment
            FileCategory newFileCategory = fileCategoryService.createFileCategory(request, null, null);

            return ResponseEntity.status(HttpStatus.CREATED).body(newFileCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create FileCategory: " + e.getMessage());
        }
    }

    @PostMapping("/assign-user")
    public ResponseEntity<?> assignUserToFileCategory(@RequestBody AssignUserRequest request) {
        try {
            FileCategory updatedFileCategory = fileCategoryService.assignUsersToFileCategory(request.getFileCategoryId(), request.getUserId());
            return ResponseEntity.ok(updatedFileCategory);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign user: " + e.getMessage());
        }
    }

    @PostMapping("/unassign-user")
    public ResponseEntity<?> unassignUserFromFileCategory(@RequestBody UnassignRequest request) {
        try {
            FileCategory updatedFileCategory = fileCategoryService.unassignUsersFromFileCategory(request.getFileCategoryId(), request.getUserId());
            return ResponseEntity.ok(updatedFileCategory);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unassign user: " + e.getMessage());
        }
    }

    @PostMapping("/assigned-users")
    public ResponseEntity<?> getAssignedUsers(@RequestBody FileCategoryAssigned request) {
        try {
            List<User> assignedUsers = fileCategoryService.getAssignedUsers(request.getFileCategoryId());
            return ResponseEntity.ok(assignedUsers);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
