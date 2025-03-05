package com.cosek.edms.authentication;

import com.cosek.edms.config.JwtService;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.organisation.Organization;
import com.cosek.edms.organisation.OrganizationRepository;
import com.cosek.edms.role.Role;
import com.cosek.edms.role.RoleRepository;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private OrganizationRepository organizationRepository;

    public AuthenticationResponse register(RegisterRequest request, Long roleID, Long organizationId) {

        Optional<Role> role = roleRepository.findById(roleID);
        HashSet<Role> roles = new HashSet<>();
        assert role.isPresent();

        roles.add(role.get());

        Organization organization = null;
        try {
            organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new NotFoundException("Organization not found"));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        var user = User.builder()
                .email(request.getEmail())
                .first_name(request.getFirst_name())
                .last_name(request.getLast_name())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .phone(request.getPhone())
                .roles(roles)
                .organization(organization)
                .build();
        userRepository.save(user);
        return generateToken(user, roles);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws NotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return generateToken(user, user.getRoles());
    }

    private AuthenticationResponse generateToken(User user, Set<Role> roles) {
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .email(user.getEmail())
                .roles(roles)
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .build();
    }

}
