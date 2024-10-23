package com.cosek.edms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.cosek.edms.helper.Constants.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final RequestLoggingFilter requestLoggingFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http  .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(AUTH_ROUTE)
                                .permitAll()
                                // User permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/users","/api/v1/users/{id}").hasAuthority(READ_USER)
                                .requestMatchers(HttpMethod.POST, "/api/v1/users/create-users").hasAuthority(CREATE_USER)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userID}/roles/{roleID}","/api/v1/users/update/{id}","/api/v1/users/roles-update/{id}").hasAuthority(UPDATE_USER)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasAuthority(DELETE_USER)


                                .requestMatchers(HttpMethod.POST, "/api/v1/departments/create-department").hasAuthority(CREATE_DEPARTMENTS)
                                .requestMatchers(HttpMethod.POST, "/api/v1/departments/assign-user-to-department").hasAuthority(UPDATE_DEPARTMENTS)


                                // Role permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/roles/all").hasAuthority(READ_ROLE)
                                .requestMatchers(HttpMethod.POST, "/api/v1/roles/create-roles").hasAuthority(CREATE_ROLE)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/roles/add-permission").hasAuthority(UPDATE_ROLE)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/roles/remove-permission").hasAuthority(UPDATE_ROLE)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/roles/update-permissions").hasAuthority(UPDATE_ROLE)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/roles/**").hasAuthority(DELETE_ROLE)

                                // Permission management permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/permissions/all").hasAuthority(READ_PERMISSION)
                                .requestMatchers(HttpMethod.POST, "/api/v1/permissions/add").hasAuthority(CREATE_PERMISSION)
                                .requestMatchers(HttpMethod.POST, "/api/v1/permissions/remove").hasAuthority(DELETE_PERMISSION)

                                // Case Studies permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/case-studies/all","/api/v1/case-studies/{id}").hasAuthority(READ_CASESTUDIES)
                                .requestMatchers(HttpMethod.POST, "/api/v1/case-studies/create-cases").hasAuthority(CREATE_CASESTUDIES)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/case-studies/assign-user","/api/v1/case-studies/update/{id}").hasAuthority(UPDATE_CASESTUDIES)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/case-studies/delete/{id}").hasAuthority(DELETE_CASESTUDIES)

                                // Files permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/files/{id}").hasAuthority(READ_FILES)
                                .requestMatchers(HttpMethod.GET, "/api/v1/files/all").hasAuthority(READ_FILES)
                                .requestMatchers(HttpMethod.GET, "/api/v1/files/all/{id}").hasAuthority(READ_FILES)
                                .requestMatchers(HttpMethod.POST, "/api/v1/files/add").hasAuthority(CREATE_FILES)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/files/update/{id}").hasAuthority(UPDATE_FILES)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/files/update-multiple").hasAuthority(UPDATE_FILES)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/files/delete/{id}").hasAuthority(DELETE_FILES)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/files/delete-multiple").hasAuthority(DELETE_FILES)


                                // Folders permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/folders/{id}").hasAuthority(READ_FOLDERS)
                                .requestMatchers(HttpMethod.GET, "/api/v1/folders/all").hasAuthority(READ_FOLDERS)
                                .requestMatchers(HttpMethod.POST, "/api/v1/folders/add").hasAuthority(CREATE_FOLDERS)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/folders/update/{id}").hasAuthority(UPDATE_FOLDERS)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/folders/update-multiple").hasAuthority(UPDATE_FOLDERS)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/folders/delete/{id}").hasAuthority(DELETE_FOLDERS)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/folders/delete-multiple").hasAuthority(DELETE_FOLDERS)

                                // Dashboard permissions
                                .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**").hasAuthority(READ_DASHBOARD)
                                .requestMatchers(HttpMethod.POST, "/api/v1/dashboard").hasAuthority(CREATE_DASHBOARD)
                                .requestMatchers(HttpMethod.PUT, "/api/v1/dashboard/**").hasAuthority(UPDATE_DASHBOARD)
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/dashboard/**").hasAuthority(DELETE_DASHBOARD)

                                .requestMatchers(HttpMethod.POST, USER_ROUTE).hasAuthority(CREATE_USER)
                                .requestMatchers(HttpMethod.GET, USER_ROUTE).hasAuthority(READ_USER)
                                .requestMatchers(HttpMethod.DELETE, USER_ROUTE).hasAuthority(DELETE_USER)
                                .requestMatchers(HttpMethod.PUT, USER_ROUTE).hasAuthority(UPDATE_USER)
                                .requestMatchers(HttpMethod.POST, ROLE_ROUTE).hasAuthority(CREATE_ROLE)
                                .requestMatchers(HttpMethod.GET, ROLE_ROUTE).hasAuthority(READ_ROLE)
                                .requestMatchers(HttpMethod.DELETE, ROLE_ROUTE).hasAuthority(DELETE_ROLE)
                                .requestMatchers(HttpMethod.PUT, ROLE_ROUTE).hasAuthority(UPDATE_ROLE)
                                .requestMatchers(HttpMethod.POST, PERMISSION_ROUTE).hasAuthority(CREATE_PERMISSION)
                                .requestMatchers(HttpMethod.GET, PERMISSION_ROUTE).hasAuthority(READ_PERMISSION)
                                .requestMatchers(HttpMethod.DELETE, PERMISSION_ROUTE).hasAuthority(DELETE_PERMISSION)
                                .requestMatchers(HttpMethod.PUT, PERMISSION_ROUTE).hasAuthority(UPDATE_PERMISSION)
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
