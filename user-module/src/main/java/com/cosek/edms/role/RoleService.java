package com.cosek.edms.role;

import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.permission.Permission;
import com.cosek.edms.permission.PermissionRepository;
import com.cosek.edms.permission.PermissionService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;


    @Autowired
    private PermissionRepository permissionRepository;

    public Role createRole(Role request) {
        return roleRepository.save(request);
    }

    public Role findOneRole(Long roleId) throws NotFoundException {
        return roleRepository.findById(roleId).orElse(null);
    }

    public Role addPermissionToRole(Long roleID, Long permID) throws NotFoundException {
        Role role = findOneRole(roleID);

        Permission permission = permissionService.findOnePermission(permID);
        Set<Permission> permissions = role.getPermissions();
        permissions.add(permission);
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    public Role removePermissionFromRole(Long roleId, Long permId) throws NotFoundException {
        // Find the role by its ID
        Role role = findOneRole(roleId);

        // Check if the role exists
        if (role == null) {
            throw new NotFoundException("Role not found with ID: " + roleId);
        }

        // Find the permission by its ID
        Permission permission = permissionService.findOnePermission(permId);

        // Check if the permission exists
        if (permission == null) {
            throw new NotFoundException("Permission not found with ID: " + permId);
        }

        // Remove the permission from the role
        Set<Permission> permissions = role.getPermissions();
        if (permissions.contains(permission)) {
            permissions.remove(permission);
            role.setPermissions(permissions);
        }

        // Save the role with updated permissions
        return roleRepository.save(role);
    }


    @Transactional
    public Role addMultiplePermissions(Long roleId, boolean status, List<Long> permissionIds) throws NotFoundException {
        // Find the role by its ID
        Role role = findOneRole(roleId);

        // Check if the role exists
        if (role == null) {
            throw new NotFoundException("Role not found with ID: " + roleId);
        }

        // Fetch permissions from the database
        List<Permission> permissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionService.findOnePermission(permissionId);
            if (permission != null) {
                permissions.add(permission);
            } else {
                throw new NotFoundException("Permission not found with ID: " + permissionId);
            }
        }

        // Handle adding permissions
        if (status) {
            for (Permission permission : permissions) {
                // Add permission only if it's not already assigned to the role
                if (!role.getPermissions().contains(permission)) {
                    role.getPermissions().add(permission);
                }
            }
        } else {
            // Handle removing permissions
            for (Permission permission : permissions) {
                // Remove permission only if it exists in the role
                role.getPermissions().remove(permission);
            }
        }

        // Save the role with updated permissions
        return roleRepository.save(role);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public Role findByRoleName(String roleName) throws NotFoundException {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
    }

    @Transactional()  // Ensures that the session stays open during the method execution
    public Role getRoleWithPermissions(String roleName) {
        Optional<Role> role = roleRepository.findByNameWithPermissions(roleName);

        return role.orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
    }

    @PostConstruct
    public void initializeRolesAndPermissions() {
        // Files permissions
        Permission readFiles = permissionRepository.findByName("READ_FILES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "READ_FILES", new HashSet<>())));
        Permission createFiles = permissionRepository.findByName("CREATE_FILES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "CREATE_FILES", new HashSet<>())));
        Permission updateFiles = permissionRepository.findByName("UPDATE_FILES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "UPDATE_FILES", new HashSet<>())));
        Permission deleteFiles = permissionRepository.findByName("DELETE_FILES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "DELETE_FILES", new HashSet<>())));

        // Folders permissions
        Permission readFolders = permissionRepository.findByName("READ_FOLDERS")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "READ_FOLDERS", new HashSet<>())));
        Permission createFolders = permissionRepository.findByName("CREATE_FOLDERS")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "CREATE_FOLDERS", new HashSet<>())));
        Permission updateFolders = permissionRepository.findByName("UPDATE_FOLDERS")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "UPDATE_FOLDERS", new HashSet<>())));
        Permission deleteFolders = permissionRepository.findByName("DELETE_FOLDERS")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "DELETE_FOLDERS", new HashSet<>())));

        // Case Studies permissions
        Permission readCaseStudies = permissionRepository.findByName("READ_CASESTUDIES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "READ_CASESTUDIES", new HashSet<>())));
        Permission createCaseStudies = permissionRepository.findByName("CREATE_CASESTUDIES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "CREATE_CASESTUDIES", new HashSet<>())));
        Permission updateCaseStudies = permissionRepository.findByName("UPDATE_CASESTUDIES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "UPDATE_CASESTUDIES", new HashSet<>())));
        Permission deleteCaseStudies = permissionRepository.findByName("DELETE_CASESTUDIES")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "DELETE_CASESTUDIES", new HashSet<>())));

        // Existing User and Role Permissions
        Permission createUser = permissionRepository.findByName("CREATE_USER")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "CREATE_USER", new HashSet<>())));
        Permission deleteUser = permissionRepository.findByName("DELETE_USER")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "DELETE_USER", new HashSet<>())));
        Permission readUser = permissionRepository.findByName("READ_USER")
                .orElseGet(() -> permissionRepository.save(new Permission(null, "READ_USER", new HashSet<>())));

        // Assign permissions to roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setPermissions(new HashSet<>(Arrays.asList(
                            readUser,
                            readFiles, createFiles, updateFiles, deleteFiles,
                            readFolders, createFolders, updateFolders, deleteFolders,
                            readCaseStudies, createCaseStudies, updateCaseStudies, deleteCaseStudies
                    )));
                    return role;
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    role.setPermissions(new HashSet<>(Arrays.asList(
                            readFiles, readFolders, readCaseStudies,createFiles
                    )));
                    return role;
                });

        // Save roles
        roleRepository.save(adminRole);
        roleRepository.save(userRole);
    }

    public Role addPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findByName(permissionName).orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    public Role removePermissionFromRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findByName(permissionName).orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }

}
