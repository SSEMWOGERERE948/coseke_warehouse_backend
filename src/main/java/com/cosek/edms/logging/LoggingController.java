package com.cosek.edms.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logging")
@CrossOrigin("*")
public class LoggingController {

    @Autowired
    private LoggingService loggingService;

    @GetMapping("/all")
    public ResponseEntity<Page<Logging>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,  // Default page number
            @RequestParam(defaultValue = "10") int size  // Default page size
    ) {
        try {
            Pageable paging = PageRequest.of(page, size);
            List<Logging> allLogs = loggingService.parseLogEntries();

            // Manually create a paginated response (since we are not using a DB)
            int start = Math.min((int) paging.getOffset(), allLogs.size());
            int end = Math.min((start + paging.getPageSize()), allLogs.size());

            Page<Logging> logPage = new PageImpl<>(allLogs.subList(start, end), paging, allLogs.size());

            return ResponseEntity.ok(logPage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
