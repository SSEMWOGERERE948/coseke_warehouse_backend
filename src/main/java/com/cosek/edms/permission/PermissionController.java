package com.cosek.edms.permission;

import com.cosek.edms.permission.Models.PermissionRequest;
import com.cosek.edms.permission.Models.RoleRequest;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleService;
import com.cosek.edms.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;


    @GetMapping("/all")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PostMapping("/role")
    public ResponseEntity<List<Permission>> getPermissionsByRole(@RequestBody RoleRequest roleRequest) {
        List<Permission> permissions = permissionService.getPermissionsByRole(roleRequest.getRoleName());
        return ResponseEntity.ok(permissions);
    }

    // Add Permission to Role
    @PostMapping("/add")
    public ResponseEntity<Role> addPermissionToRole(@RequestBody PermissionRequest permissionRequest) {
        Role updatedRole = roleService.addPermissionToRole(permissionRequest.getRoleName(), permissionRequest.getPermissionName());
        return ResponseEntity.ok(updatedRole);
    }

    // Remove Permission from Role
    @PostMapping("/remove")
    public ResponseEntity<Role> removePermissionFromRole(@RequestBody PermissionRequest permissionRequest) {
        Role updatedRole = roleService.removePermissionFromRole(permissionRequest.getRoleName(), permissionRequest.getPermissionName());
        return ResponseEntity.ok(updatedRole);
    }
}