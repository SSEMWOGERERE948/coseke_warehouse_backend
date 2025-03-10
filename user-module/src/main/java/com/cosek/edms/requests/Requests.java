package com.cosek.edms.requests;

import com.cosek.edms.files.Files;
import com.cosek.edms.organisation.Organization;
import com.cosek.edms.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requests")
public class Requests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @JsonBackReference("file-requests") // Prevents infinite loop
    private Files file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-requests") // Prevents infinite loop
    private User user;

    @Column(nullable = false)
    private String requestType; // "Check Out" or "Check In"

    @Column(nullable = false)
    private String status; // "Pending", "Approved", "Completed"

    @Column(nullable = false)
    private LocalDateTime requestDate; // Stores when the request was made

    @Column(nullable = true)
    private LocalDateTime completedDate; // Stores when the request was completed

    @Column(nullable = false)
    private int boxNumber; // ✅ Added Box Number

    @Column(name = "file_id", insertable = false, updatable = false)
    private Long fileId; // ✅ Store file ID separately

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization; // ✅ Added Organization Reference
}
