package com.cosek.edms.permission;

import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission findOnePermission(Long permID) throws NotFoundException {
        return permissionRepository.findById(permID)
                .orElseThrow(() -> new NotFoundException("Permission with ID: " + permID + " not found"));
    }

    public Permission updatePermission(Long permID, Permission request) throws NotFoundException {
        Permission permission = findOnePermission(permID);
        permission.setName(request.getName());
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

//    public User assignPermissions(Long userId, List<Long> permissionIds) throws NotFoundException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
//
//        Set<Permission> currentPermissions = user.getPermissions() != null ? user.getPermissions() : new HashSet<>();
//
//        // Add the permissions to the user
//        for (Long permId : permissionIds) {
//            Permission permission = findOnePermission(permId);  // Fix: calling method within service
//            currentPermissions.add(permission);
//        }
//
//        user.setPermissions(currentPermissions);
//        return userRepository.save(user);
//    }
//
//    public User removePermissions(Long userId, List<Long> permissionIds) throws NotFoundException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
//
//        Set<Permission> currentPermissions = user.getPermissions();
//
//        // Remove the permissions from the user
//        for (Long permId : permissionIds) {
//            Permission permission = findOnePermission(permId);  // Fix: calling method within service
//            currentPermissions.remove(permission);
//        }
//
//        user.setPermissions(currentPermissions);
//        return userRepository.save(user);
//    }

    public Permission findByName(String name) throws NotFoundException {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Permission with name: " + name + " not found"));
    }

    public List<Permission> getPermissionsByRole(String roleName) {
        return permissionRepository.findPermissionsByRole(roleName);
    }

}
