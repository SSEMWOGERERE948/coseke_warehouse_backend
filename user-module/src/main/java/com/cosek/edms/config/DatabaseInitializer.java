package com.cosek.edms.config;

import com.cosek.edms.permission.Permission;
import com.cosek.edms.permission.PermissionRepository;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cosek.edms.helper.Constants.*;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            List<Permission> permissions = initializePermissions();
            Role superAdminRole = initializeSuperAdminRole(permissions);
            Role adminRole = initializeAdminRole(permissions);
            Role userRole = initializeUserRole(permissions);
            initializeAdminUser(superAdminRole);
            initializeAdmUser(adminRole);
            initializeUser(userRole);

        };
    }

    private List<Permission> initializePermissions() {
        return Arrays.asList(
                // Existing permissions
                ensurePermission(READ_PERMISSION),
                ensurePermission(CREATE_PERMISSION),
                ensurePermission(UPDATE_PERMISSION),
                ensurePermission(DELETE_PERMISSION),
                ensurePermission(READ_ROLE),
                ensurePermission(CREATE_ROLE),
                ensurePermission(UPDATE_ROLE),
                ensurePermission(DELETE_ROLE),
                ensurePermission(CREATE_USER),
                ensurePermission(READ_USER),
                ensurePermission(UPDATE_USER),
                ensurePermission(DELETE_USER),

                // New Dashboard permissions
                ensurePermission(READ_DASHBOARD),
                ensurePermission(CREATE_DASHBOARD),
                ensurePermission(UPDATE_DASHBOARD),
                ensurePermission(DELETE_DASHBOARD),

                // New Files permissions
                ensurePermission(READ_FILES),
                ensurePermission(CREATE_FILES),
                ensurePermission(UPDATE_FILES),
                ensurePermission(DELETE_FILES),

                // New Folders permissions
                ensurePermission(READ_FOLDERS),
                ensurePermission(CREATE_FOLDERS),
                ensurePermission(UPDATE_FOLDERS),
                ensurePermission(DELETE_FOLDERS),

                // New Case Studies permissions
                ensurePermission(READ_CASESTUDIES),
                ensurePermission(CREATE_CASESTUDIES),
                ensurePermission(UPDATE_CASESTUDIES),
                ensurePermission(DELETE_CASESTUDIES),

                // New Requests permissions
                ensurePermission(READ_REQUESTS),
                ensurePermission(CREATE_REQUESTS),
                ensurePermission(UPDATE_REQUESTS),
                ensurePermission(DELETE_REQUESTS),

                ensurePermission(READ_DEPARTMENTS),
                ensurePermission(CREATE_DEPARTMENTS),
                ensurePermission(UPDATE_DEPARTMENTS),
                ensurePermission(DELETE_DEPARTMENTS)
        );
    }

    private Permission ensurePermission(String permissionName) {
        return permissionRepository.findByName(permissionName)
                .orElseGet(() -> permissionRepository.save(new Permission(null, permissionName, new HashSet<>())));
    }

    private Role initializeSuperAdminRole(List<Permission> permissions) {
        return roleRepository.findByName(SUPER_ADMIN)
                .orElseGet(() -> roleRepository.save(
                        new Role(null, SUPER_ADMIN, null, new HashSet<>(permissions))
                ));
    }

    private Role initializeAdminRole(List<Permission> permissions) {
        Set<Permission> adminPermissions = permissions.stream()
                .filter(permission -> List.of(
                        CREATE_USER, READ_USER, UPDATE_USER, DELETE_USER,
                        CREATE_ROLE, READ_ROLE, UPDATE_ROLE, DELETE_ROLE,CREATE_FILES
                ).contains(permission.getName()))
                .collect(Collectors.toSet());

        return roleRepository.findByName(ADMIN)
                .orElseGet(() -> roleRepository.save(
                        new Role(null, ADMIN, null, adminPermissions)
                ));
    }

    private Role initializeUserRole(List<Permission> permissions) {
        Set<Permission> userPermissions = permissions.stream()
                .filter(permission -> permission.getName().equals(READ_USER))
                .collect(Collectors.toSet());

        return roleRepository.findByName(USER)
                .orElseGet(() -> roleRepository.save(
                        new Role(null, USER, null, userPermissions)
                ));
    }

    private void initializeAdminUser(Role superAdminRole) {
        HashSet<Role> roles = new HashSet<>();
        roles.add(superAdminRole);
        userRepository.findByEmail(ADMIN_EMAIL).orElseGet(() -> {
            User admin = User.builder()
                    .first_name(ADMIN_FIRST_NAME)
                    .last_name(ADMIN_LAST_NAME)
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .phone(ADMIN_PHONE)
                    .address(ADMIN_COUNTRY)
                    .roles(roles)
                    .build();
            return userRepository.save(admin);
        });
    }
    private void initializeAdmUser(Role adminRole) {
        HashSet<Role> roles = new HashSet<>();
        roles.add(adminRole);
    }
    private void initializeUser(Role userRole) {
        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);
    }
}
