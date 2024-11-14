package com.cosek.edms.departments;

import com.cosek.edms.folders.Folders;
import com.cosek.edms.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, unique = true)
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

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


    public Long getDepartmentId(){
        return id;
    }


    // Assuming a Many-to-Many relationship between Department and Folders
    @ManyToMany
    @JoinTable(
            name = "department_folders",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private List<Folders> folders;

}