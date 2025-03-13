//package com.cosek.edms.requests;
//
//import com.cosek.edms.MailingService.MailingDetails;
//import com.cosek.edms.MailingService.MailingServiceService;
//import com.cosek.edms.files.Files;
//import com.cosek.edms.files.FilesRepository;
//import com.cosek.edms.user.User;
//import com.cosek.edms.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.nio.file.AccessDeniedException;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//@Service
//@RequiredArgsConstructor
//public class RequestsService {
//
//    private final RequestsRepository requestsRepository;
//    private final FilesRepository filesRepository;
//    private final UserRepository userRepository;
//    private final MailingServiceService mailingService;
//
//    private User getLoggedInUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        return userRepository.findByEmail(username).orElseThrow(() ->
//                new IllegalArgumentException("User not found")
//        );
//    }
//
////    public Requests createRequest(Requests requests) {
////        Files file = filesRepository.findById(requests.getFiles().getId()).orElseThrow();
////        User loggedInUser = getLoggedInUser();
////        Requests request = Requests.builder()
////                .files(file)
////                .user(loggedInUser)
////                .stage("Officer")
////                .state("In Progress")
////                .returnDate(requests.getReturnDate())
////                .createdBy(requests.getCreatedBy())
////                .build();
////        List<User> managers = userRepository.findByRoleName("MANAGER");
////        User requestUser = request.getUser();
////        String[] recipientEmails = Stream.concat(
////                Stream.of(requestUser.getEmail()), // Add requestUser's email
////                managers.stream().map(User::getEmail) // Add managers' emails
////        ).toArray(String[]::new);
////
////        MailingDetails mailingDetails = MailingDetails.builder()
////                .recipient(recipientEmails)
////                .subject("File Checkout Request Confirmation")
////                .msgBody(String.format(
////                        "Dear %s,%n%n" +
////                                "Your request to check out the file has been successfully submitted. Here are the details of your request:%n%n" +
////                                "PID: %s%n" +
////                                "Requested Return Date: %s%n%n" +
////                                "Your request is currently being reviewed at the 'Officer' stage and is marked as 'In Progress'.%n%n" +
////                                "You can check the status of your request at any time using the following link:%n" +
////                                "http://10.1.0.115/dashboard/tasks/my-requests%n%n" +
////                                "Thank you,%n" +
////                                "Records Management Team",
////                        loggedInUser.getFirst_name() + " " + loggedInUser.getLast_name(),
////                        file.getPID(),
////                        requests.getReturnDate().toLocalDate().toString()
////                ))
////                .build();
////        mailingService.sendMail(mailingDetails, "rams@baylor-uganda.org");
////        return requestsRepository.save(request);
////    }
//
//    public Optional<Requests> getRequestById(Long requestId) {
//        return requestsRepository.findById(requestId);
//    }
//
//    public List<Requests> getAllRequests() {
//        return requestsRepository.findAll();
//    }
//
//    public Optional<Requests> changeStage(Long requestId, String newStage) {
//        Optional<Requests> requestOptional = requestsRepository.findById(requestId);
//        if (requestOptional.isPresent()) {
//            Requests request = requestOptional.get();
//            request.setStage(newStage);
//            Requests updatedRequest = requestsRepository.save(request);
//            List<User> managers = userRepository.findByRoleName("SUPERVISOR");
//            User requestUser = request.getUser();
//            String[] recipientEmails = Stream.concat(
//                    Stream.of(requestUser.getEmail()), // Add requestUser's email
//                    managers.stream().map(User::getEmail) // Add managers' emails
//            ).toArray(String[]::new);
//
//            MailingDetails mailingDetails = MailingDetails.builder()
//                    .recipient(recipientEmails)
//                    .subject("Request Status Update")
//                    .msgBody(String.format(
//                            "Dear %s,%n%n" +
//                                    "Your request has been moved to the '%s' stage.%n%n" +
//                                    "You can check the status of your request using the following link:%n" +
//                                    "http://10.1.0.115/dashboard/tasks/my-requests%n%n" +
//                                    "Thank you,%n" +
//                                    "Records Management Team",
//                            requestUser.getFirst_name() + " " + requestUser.getLast_name(),
//                            newStage
//                    ))
//                    .build();
//            mailingService.sendMail(mailingDetails, "rams@baylor-uganda.org");
//            return Optional.of(updatedRequest);
//        }
//        return Optional.empty();
//    }
//
//    @Transactional
//    public Optional<Requests> approveRequest(Long requestId) throws AccessDeniedException {
//        Optional<Requests> requestOptional = requestsRepository.findById(requestId);
//        if (requestOptional.isPresent()) {
//            if(Objects.equals(requestOptional.get().getFiles().getStatus(), "Unavailable")) {
//                throw new AccessDeniedException("Cannot checkout file because it is currently unavailable!");
//            }
//            Requests request = requestOptional.get();
//            request.setStage("Approved");
//            request.setState("Complete");
//            request.getFiles().setStatus("Unavailable");
//            Requests updatedRequest = requestsRepository.save(request);
//
//            User requestUser = request.getUser();
//            MailingDetails mailingDetails = MailingDetails.builder()
//                    .recipient(new String[]{getLoggedInUser().getEmail()})
//                    .subject("Request Approved")
//                    .msgBody(String.format(
//                            "Dear %s,%n%n" +
//                                    "Your request has been approved and is now complete.%n%n" +
//                                    "You can view details at:%n" +
//                                    "http://10.1.0.115/dashboard/tasks/my-requests%n%n" +
//                                    "Thank you,%n" +
//                                    "Records Management Team",
//                            requestUser.getFirst_name() + " " + requestUser.getLast_name()
//                    ))
//                    .build();
//            mailingService.sendMail(mailingDetails, "rams@baylor-uganda.org");
//            return Optional.of(updatedRequest);
//        }
//        return Optional.empty();
//    }
//
////    public Optional<Requests> rejectRequest(Long requestId, String reason) {
////        Optional<Requests> requestOptional = requestsRepository.findById(requestId);
////        if (requestOptional.isPresent()) {
////            Requests request = requestOptional.get();
////            request.setStage("Rejected");
////            request.setState("Complete");
////            request.setReason(reason);
////            Requests updatedRequest = requestsRepository.save(request);
////
////            User requestUser = request.getUser();
////            MailingDetails mailingDetails = MailingDetails.builder()
////                    .recipient(new String[] {getLoggedInUser().getEmail()})
////                    .subject("Request Rejected")
////                    .msgBody(String.format(
////                            "Dear %s,%n%n" +
////                                    "Your request has for file with PID %s, been rejected for the following reason:%n" +
////                                    "%s%n%n" +
////                                    "If you have any questions, please contact support.%n%n" +
////                                    "Thank you,%n" +
////                                    "Records Management Team",
////                            requestUser.getFirst_name() + " " + requestUser.getLast_name(),
////                            request.getFiles().getPID(),
////                            reason
////                    ))
////                    .build();
////            mailingService.sendMail(mailingDetails, "rams@baylor-uganda.org");
////            return Optional.of(updatedRequest);
////        }
////        return Optional.empty();
////    }
//}