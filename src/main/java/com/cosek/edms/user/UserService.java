package com.cosek.edms.user;

import com.cosek.edms.MailingService.MailingDetails;
import com.cosek.edms.MailingService.MailingServiceService;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.organisation.Organization;
import com.cosek.edms.organisation.OrganizationRepository;
import com.cosek.edms.permission.PermissionService;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleService;
import com.cosek.edms.user.Models.CreateUserRequest;
import com.cosek.edms.user.Models.UpdateUserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.cosek.edms.helper.Constants.FAILED_DELETION;
import static com.cosek.edms.helper.Constants.SUCCESSFUL_DELETION;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final MailingServiceService mailingService;
    private final OrganizationRepository organizationRepository; // ✅ Inject Organization Repository

    private final Map<String, PasswordResetToken> resetTokens = new HashMap<>(); // To store tokens temporarily

    public User createUser(CreateUserRequest request) throws NotFoundException {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        User user = User.builder()
                .first_name(request.getFirst_name())
                .last_name(request.getLast_name())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.emptySet())
                .build();

        return userRepository.save(user);
    }

    public String forgotPassword(String email) throws NotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, new PasswordResetToken(user, token, new Date(System.currentTimeMillis() + 15 * 60 * 1000))); // 15 min expiry
        MailingDetails mailingDetails = MailingDetails.builder()
                .recipient(new String[]{user.getEmail()})
                .subject("Password Reset Request")
                        .msgBody("To reset your password, click the link below:\n" +
                                "http://10.1.0.115/reset-password?token=" + token).build();
        mailingService.sendMail(mailingDetails, "rams@baylor-uganda.org");
        return "Password reset email sent";
    }

    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokens.get(token);

        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate the token after use
        resetTokens.remove(token);

        return "Password successfully reset";
    }

    public String updatePassword(Long userId, String currentPassword, String newPassword) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        // Verify the current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set and encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password successfully updated";
    }

    public User assignUserTypes(Long userId, List<String> userTypes) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<Role> roles = user.getRoles();

        for (String userType : userTypes) {
            Role assignedRole;
            if ("manager".equalsIgnoreCase(userType)) {
                assignedRole = roleService.findByRoleName("MANAGER");
            }
            else {
                throw new IllegalArgumentException("Invalid user type: " + userType);
            }

            roles.add(assignedRole);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User unassignUserTypes(Long userId, List<String> userTypes) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<Role> currentRoles = user.getRoles();
        Set<Role> rolesToRemove = new HashSet<>();

        for (String userType : userTypes) {
            Role roleToRemove;
            if ("manager".equalsIgnoreCase(userType)) {
                roleToRemove = roleService.findByRoleName("MANAGER");
            } else if ("user".equalsIgnoreCase(userType)) {
                roleToRemove = roleService.findByRoleName("USER");
            }
            else if ("supervisor".equalsIgnoreCase(userType)) {
                roleToRemove = roleService.findByRoleName("SUPERVISOR");
            }else {
                throw new IllegalArgumentException("Invalid user type: " + userType);
            }

            if (roleToRemove != null) {
                rolesToRemove.add(roleToRemove);
            }
        }

        currentRoles.removeAll(rolesToRemove);
        user.setRoles(currentRoles);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UpdateUserRequest request, Long id) throws NotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setFirst_name(request.getFirst_name());
        user.setLast_name(request.getLast_name());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // ✅ Handle Organization update
        if (request.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new NotFoundException("Organization not found with id: " + request.getOrganizationId()));
            user.setOrganization(organization);
        }

        return userRepository.save(user);
    }
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findOneUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addRoleToUser(Long userID, Long roleId) throws NotFoundException {
        User user = findOneUser(userID);
        Role role = roleService.findOneRole(roleId);
        Set<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User updateRoles(Long userID, List<Role> roles) throws NotFoundException {
        Set<Role> newRoles = new HashSet<>();
        User user = findOneUser(userID);

        for (Role role : roles) {
            Role fetchedRole = roleService.findOneRole(role.getId());
            if (fetchedRole != null) {
                newRoles.add(fetchedRole);
            }
        }

        user.setRoles(newRoles);
        return userRepository.save(user);
    }

    public Map<String, Object> deleteUser(Long id) {
        userRepository.deleteById(id);
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return deleteResponse(false, FAILED_DELETION, id);
        }
        return deleteResponse(true, SUCCESSFUL_DELETION, id);
    }

    private Map<String, Object> deleteResponse(boolean status, String message, Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", status);
        response.put("messages", message);
        response.put("id", id);
        return response;
    }

    private static class PasswordResetToken {
        private final User user;
        private final String token;
        private final Date expiryDate;

        PasswordResetToken(User user, String token, Date expiryDate) {
            this.user = user;
            this.token = token;
            this.expiryDate = expiryDate;
        }

        public User getUser() {
            return user;
        }

        public boolean isExpired() {
            return new Date().after(expiryDate);
        }
    }
}
