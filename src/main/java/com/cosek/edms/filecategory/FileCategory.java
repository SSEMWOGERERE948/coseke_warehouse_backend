package com.cosek.edms.filecategory;

import com.cosek.edms.files.Files;
import com.cosek.edms.user.User;
import com.cosek.edms.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "file_category")
public class FileCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "fileCategory", cascade = CascadeType.ALL)
    private List<Files> files;

    // Many-to-many relationship with users
    @ManyToMany
    @JoinTable(
            name = "file_category_user",
            joinColumns = @JoinColumn(name = "file_category_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    // Many-to-many relationship with roles
    @ManyToMany
    @JoinTable(
            name = "file_category_role",
            joinColumns = @JoinColumn(name = "file_category_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
