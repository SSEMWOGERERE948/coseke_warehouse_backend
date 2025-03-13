package com.cosek.edms.filecategory;

import com.cosek.edms.user.User;
import com.cosek.edms.role.Role;
import com.cosek.edms.filecategory.Modals.FileCategoryRequest;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.role.RoleRepository;
import com.cosek.edms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileCategoryService {

    @Autowired
    private FileCategoryRepository fileCategoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  UserRepository userRepository;

    // Fetch all case studies
    public List<FileCategory> getAllFileCategories() {
        return fileCategoryRepository.findAll();
    }

    // Get a case study by ID
    public Optional<FileCategory> getFileCategoryById(Long id) {
        return fileCategoryRepository.findById(id);
    }

    public FileCategory findFileCategoryById(Long fileCategoryId) {
        return fileCategoryRepository.findFileCategoryById(fileCategoryId);
    }

    // Save a new case study
    public FileCategory saveFileCategory(FileCategory fileCategory) {
        return fileCategoryRepository.save(fileCategory);
    }

    // Update an existing case study
    public FileCategory updateFileCategory(Long id, FileCategory updatedFileCategory) {
        Optional<FileCategory> fileCategoryOptional = fileCategoryRepository.findById(id);
        if (fileCategoryOptional.isEmpty()) {
            throw new RuntimeException("Case study not found");
        }

        FileCategory fileCategory = fileCategoryOptional.get();
        fileCategory.setName(updatedFileCategory.getName());
        fileCategory.setDescription(updatedFileCategory.getDescription());
        fileCategory.setUsers(updatedFileCategory.getUsers());
        return fileCategoryRepository.save(fileCategory);
    }

    // Delete a case study
    public void deleteFileCategory(Long id) {
        fileCategoryRepository.deleteById(id);
    }

    public FileCategory createFileCategory(FileCategoryRequest request, Set<Role> roles, Set<User> users) {
        FileCategory newFileCategory = FileCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .users(users == null ? new HashSet<>() : users)
                .roles(roles == null ? new HashSet<>() : roles)  // Assign roles set
                .build();

        return fileCategoryRepository.save(newFileCategory);
    }

    // Find a role by ID for assignment
    public Role findOneRole(Long roleId) throws NotFoundException {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public FileCategory assignUsersToFileCategory(Long fileCategoryId, List<Long> userIds) throws NotFoundException {
        Optional<FileCategory> fileCategoryOptional = fileCategoryRepository.findById(fileCategoryId);
        if (fileCategoryOptional.isEmpty()) {
            throw new NotFoundException("Case Study not found");
        }

        FileCategory fileCategory = fileCategoryOptional.get();
        List<User> usersToAdd = new ArrayList<>();

        for (Long userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("User with ID " + userId + " not found");
            }
            usersToAdd.add(userOptional.get());
        }

        // Add all users to the case study's set of users
        fileCategory.getUsers().addAll(usersToAdd);

        // Persist the updated case study
        return fileCategoryRepository.save(fileCategory);
    }

    public FileCategory unassignUsersFromFileCategory(Long fileCategoryId, Long userId) throws NotFoundException {
        // Fetch the FileCategory by ID
        Optional<FileCategory> fileCategoryOptional = fileCategoryRepository.findById(fileCategoryId);
        if (fileCategoryOptional.isEmpty()) {
            throw new NotFoundException("Case Study not found");
        }

        FileCategory fileCategory = fileCategoryOptional.get();

        // Fetch the User by ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }

        User userToRemove = userOptional.get();

        // Check if the user is assigned to the case study
        if (!fileCategory.getUsers().contains(userToRemove)) {
            throw new NotFoundException("User with ID " + userId + " is not assigned to this case study");
        }

        // Remove the user from the case study's set of users
        fileCategory.getUsers().remove(userToRemove);

        // Persist the updated case study
        return fileCategoryRepository.save(fileCategory);
    }

    public List<User> getAssignedUsers(Long fileCategoryId) throws NotFoundException {
        // Find user IDs assigned to the case study
        List<Long> userIds = fileCategoryRepository.findAssignedUserIdsByFileCategoryId(fileCategoryId);

        if (userIds.isEmpty()) {
            throw new NotFoundException("No users assigned to this case study");
        }

        // Fetch the User entities based on the user IDs
        return userRepository.findAllById(userIds);
    }

}
