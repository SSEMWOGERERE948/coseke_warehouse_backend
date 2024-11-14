package com.cosek.edms.files;

import com.cosek.edms.casestudy.CaseStudy;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private String PID;
    private int boxNumber;
    private String status;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference("user-files") // Unique value for user reference
    private User responsibleUser;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private Folders folder;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "case_study_id", referencedColumnName = "id")
    @JsonBackReference("caseStudy-files") // Unique value for case study reference
    private CaseStudy caseStudy;

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

}
