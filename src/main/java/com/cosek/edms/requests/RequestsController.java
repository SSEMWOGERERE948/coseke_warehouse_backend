//package com.cosek.edms.requests;
//
//import com.cosek.edms.files.Files;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.nio.file.AccessDeniedException;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("api/v1/requests")
//@CrossOrigin(origins = "*")
//@RequiredArgsConstructor
//public class RequestsController {
//
//    private final RequestsService requestsService;
//
//    // Create a new request
////    @PostMapping
////    public ResponseEntity<Requests> createRequest(@RequestBody Requests requests) {
////        Requests createdRequest = requestsService.createRequest(requests);
////        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
////    }
//
//    // Change the stage of a request
//    @PutMapping("/{requestId}/stage")
//    public ResponseEntity<Requests> changeStage(@PathVariable Long requestId, @RequestParam String newStage) {
//        Optional<Requests> updatedRequest = requestsService.changeStage(requestId, newStage);
//        return updatedRequest.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    // Approve a request
//    @PutMapping("/{requestId}/approve")
//    public ResponseEntity<?> approveRequest(@PathVariable Long requestId) {
//        try {
//            Optional<Requests> approvedRequest = requestsService.approveRequest(requestId);
//
//            if (approvedRequest.isPresent()) {
//                approvedRequest.get().getFiles().setStatus("Unavailable");
//                approvedRequest.get().setStage("Approved");
//                return ResponseEntity.ok(approvedRequest.get());
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.NOT_FOUND)
//                        .body("Request with ID " + requestId + " not found");
//            }
//
//        } catch (AccessDeniedException e) {
//            // Get the actual error message from the exception
//            String errorMessage = e.getMessage() != null ? e.getMessage() : "Access denied: You don't have permission to approve this request";
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(errorMessage);
//
//        } catch (Exception e) {
//            // Get the actual error message from the exception
//            String errorMessage = e.getMessage() != null ? e.getMessage() : "An unexpected error occurred";
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(errorMessage);
//        }
//    }
//    // Reject a request
////    @PutMapping("/{requestId}/reject")
////    public ResponseEntity<Requests> rejectRequest(@PathVariable Long requestId, @RequestParam String reason) {
////        Optional<Requests> approvedRequest = requestsService.rejectRequest(requestId, reason);
////        approvedRequest.get().setStage("Rejected");
////        return approvedRequest.map(ResponseEntity::ok)
////                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
////    }
//
//    // Get a request by ID
//    @GetMapping("/{requestId}")
//    public ResponseEntity<Requests> getRequestById(@PathVariable Long requestId) {
//        Optional<Requests> request = requestsService.getRequestById(requestId);
//        return request.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    // Get all requests
//    @GetMapping
//    public ResponseEntity<List<Requests>> getAllRequests() {
//        List<Requests> requests = requestsService.getAllRequests();
//        return ResponseEntity.ok(requests);
//    }
//}
