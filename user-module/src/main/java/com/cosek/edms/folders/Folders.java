package com.cosek.edms.folders;

import com.cosek.edms.files.Files;
import com.cosek.edms.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;

@Data
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Folders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String folderName;
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<Files> files;

    @CreatedDate
    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name="lastModifiedDate", nullable = true)
    private LocalDateTime lastModifiedDateTime;

    @LastModifiedBy
    @Column(name = "lastModifiedBy", nullable = true)
    private Long lastModifiedBy;

    @CreatedBy
    @Column(name="createdBy", nullable = false, updatable = false)
    private Long createdBy;

    @ManyToOne
    @JoinColumn(name = "responsible_user_id", nullable = true)
    @JsonIgnore  // Add this to prevent user serialization
    private User responsibleUser;


}
