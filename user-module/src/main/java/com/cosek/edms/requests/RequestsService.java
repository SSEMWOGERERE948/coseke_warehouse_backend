package com.cosek.edms.requests;

import com.cosek.edms.files.Files;
import com.cosek.edms.user.User;
import com.cosek.edms.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestsService {

    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository; // Add this line to inject UserRepository

    // Get the currently logged-in user
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username).orElseThrow(() ->
                new IllegalArgumentException("User not found")
        );
    }

    // Create a request
    public Requests createRequest(Files files) {
        User loggedInUser = getLoggedInUser(); // Get the logged-in user
        Requests request = Requests.builder()
                .files(files)
                .user(loggedInUser) // Set the logged-in user as the user for this request
                .stage("Officer") // Initial stage
                .build();
        return requestsRepository.save(request);
    }

    // Get a request by ID
    public Optional<Requests> getRequestById(Long requestId) {
        return requestsRepository.findById(requestId);
    }

    // Get all requests
    public List<Requests> getAllRequests() {
        return requestsRepository.findAll();
    }

    // Change the stage of the request
    public Optional<Requests> changeStage(Long requestId, String newStage) {
        Optional<Requests> requestOptional = requestsRepository.findById(requestId);
        if (requestOptional.isPresent()) {
            Requests request = requestOptional.get();
            request.setStage(newStage);
            return Optional.of(requestsRepository.save(request));
        }
        return Optional.empty();
    }

    // Approve the request (final stage)
    public Optional<Requests> approveRequest(Long requestId) {
        return changeStage(requestId, "Approved");
    }
}
