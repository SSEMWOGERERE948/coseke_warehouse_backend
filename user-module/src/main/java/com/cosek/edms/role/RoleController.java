package com.cosek.edms.role;

import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.permission.Permission;
import com.cosek.edms.role.Models.MultipleUpdate;
import com.cosek.edms.role.Models.PermissionUpdateRequest;
import com.cosek.edms.role.Models.UserRoleRequest;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import com.cosek.edms.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;


    // Create a new role
    @PostMapping("/roles/create-roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role response = roleService.createRole(role);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    // List all roles
    @GetMapping("/roles/all")
    public ResponseEntity<List<Role>> listRoles() {
        List<Role> response = roleService.listRoles();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    // Add a permission to a role using @RequestBody
    @PutMapping("/roles/add-permission")
    public ResponseEntity<Role> addPermissionToRole(
            @RequestBody PermissionUpdateRequest request) throws NotFoundException {
        Role response = roleService.addPermissionToRole(request.getRoleId(), request.getPermissionId());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    // Remove a permission from a role using @RequestBody
    @PutMapping("/roles/remove-permission")
    public ResponseEntity<Role> removePermissionFromRole(
            @RequestBody PermissionUpdateRequest request) throws NotFoundException {
        Role response = roleService.removePermissionFromRole(request.getRoleId(), request.getPermissionId());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
    }

    @PutMapping("/roles/update-permissions")
    public ResponseEntity<Role> addMultiplePermissions(
            @RequestBody MultipleUpdate update) {
        try {
            List<Long> permissionIds = update.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(roleService.addMultiplePermissions(update.getRoleId(), update.isStatus(), permissionIds));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/assign-userType")
    public ResponseEntity<?> assignUserTypes(@RequestBody UserRoleRequest request) {
        try {
            Long userId = request.getUserId();
            List<String> userTypes = request.getUserTypes();

            if (userTypes == null || userTypes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User types list cannot be empty.");
            }

            // Assign multiple user types and return the updated user
            User updatedUser = userService.assignUserTypes(userId, userTypes);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }


    @PostMapping("/unassign-userType")
    public ResponseEntity<?> unassignUserTypes(@RequestBody UserRoleRequest request) {
        try {
            Long userId = request.getUserId();
            List<String> userTypes = request.getUserTypes();

            if (userTypes == null || userTypes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User types list cannot be empty.");
            }

            // Unassign user types and return the updated user
            User updatedUser = userService.unassignUserTypes(userId, userTypes);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

}
