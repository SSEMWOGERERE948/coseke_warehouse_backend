    package com.cosek.edms.files;

    import com.cosek.edms.filecategory.FileCategory;
    import com.cosek.edms.folders.Folders;
    import com.cosek.edms.locations.StorageLocation;
    import com.cosek.edms.organisation.Organization;
    import com.cosek.edms.requests.Requests;
    import com.cosek.edms.user.User;
    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.CreatedBy;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.LastModifiedBy;
    import org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EntityListeners(AuditingEntityListener.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Files {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private int boxNumber;

        @Column(nullable = false)
        private String status = "Available"; // ✅ Default to Available

        @Column(name = "checked_out_by", nullable = true)
        private Long checkedOutBy;


        @ManyToOne(cascade = CascadeType.DETACH)
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        @JsonBackReference("user-files")
        private User responsibleUser;

        @ManyToOne(cascade = CascadeType.DETACH)
        @JoinColumn(name = "folder_id", referencedColumnName = "id")
        private Folders folder;

        @ManyToOne(cascade = CascadeType.DETACH)
        @JoinColumn(name = "case_study_id", referencedColumnName = "id")
        @JsonBackReference("fileCategory-files")
        private FileCategory fileCategory;

        // Link files to an archival box
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "archival_box_id", referencedColumnName = "id")
        @JsonIgnore // Prevents serialization of proxy objects
        private StorageLocation archivalBox;

        @ManyToOne(cascade = CascadeType.DETACH)
        @JoinColumn(name = "organization_id", referencedColumnName = "id") // ✅ New field
        private Organization organization; // ✅ Link file to organization


        @CreatedDate
        @Column(name = "createdDate", nullable = false, updatable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        @Column(name = "lastModifiedDate", nullable = true)
        private LocalDateTime lastModifiedDateTime;

        @LastModifiedBy
        @Column(name = "lastModifiedBy", nullable = true)
        private Long lastModifiedBy;

        @CreatedBy
        @Column(name = "createdBy", nullable = false, updatable = false)
        private Long createdBy;

        @Column(columnDefinition = "TEXT")
        private String metadataJson; // Stores all Excel columns as JSON

        @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JsonManagedReference("file-requests")
        private List<Requests> requests = new ArrayList<>();
    }
