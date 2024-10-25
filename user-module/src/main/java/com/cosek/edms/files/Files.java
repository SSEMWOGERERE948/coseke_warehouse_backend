package com.cosek.edms.files;

import com.cosek.edms.casestudy.CaseStudy;
import com.cosek.edms.folders.Folders;
import com.cosek.edms.user.User;
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

@Data
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true) // Handles unknown fields
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String PIDInfant;
    private String PIDMother;
    private int boxNumber;
    private String status;


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"files"}) // Prevents looping with User entity
    private User responsibleUser;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"files"}) // Prevents looping with Folders entity
    private Folders folder;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "case_study_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"files"}) // Prevents looping with CaseStudy entity
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
    @Column(name="createdBy", nullable = false, updatable = false)
    private Long createdBy;
}
