package com.cosek.edms.requests;

import com.cosek.edms.files.Files;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/requests")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RequestsController {

    private final RequestsService requestsService;

    // Create a new request
    @PostMapping
    public ResponseEntity<Requests> createRequest(@RequestBody Requests requests) {
        Requests createdRequest = requestsService.createRequest(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    // Change the stage of a request
    @PatchMapping("/{requestId}/stage")
    public ResponseEntity<Requests> changeStage(@PathVariable Long requestId, @RequestParam String newStage) {
        Optional<Requests> updatedRequest = requestsService.changeStage(requestId, newStage);
        return updatedRequest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Approve a request
    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<Requests> approveRequest(@PathVariable Long requestId) {
        Optional<Requests> approvedRequest = requestsService.approveRequest(requestId);
        approvedRequest.get().getFiles().setStatus("Unavailable");
        approvedRequest.get().setStage("Approved");
        return approvedRequest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get a request by ID
    @GetMapping("/{requestId}")
    public ResponseEntity<Requests> getRequestById(@PathVariable Long requestId) {
        Optional<Requests> request = requestsService.getRequestById(requestId);
        return request.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get all requests
    @GetMapping
    public ResponseEntity<List<Requests>> getAllRequests() {
        List<Requests> requests = requestsService.getAllRequests();
        return ResponseEntity.ok(requests);
    }
}
