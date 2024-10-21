package com.cosek.edms.casestudy;

import com.cosek.edms.user.User;
import com.cosek.edms.role.Role;
import com.cosek.edms.casestudy.Modals.CaseStudyRequest;
import com.cosek.edms.exception.NotFoundException;
import com.cosek.edms.role.RoleRepository;
import com.cosek.edms.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CaseStudyService {

    @Autowired
    private CaseStudyRepository caseStudyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  UserRepository userRepository;

    // Fetch all case studies
    public List<CaseStudy> getAllCaseStudies() {
        return caseStudyRepository.findAll();
    }

    // Get a case study by ID
    public Optional<CaseStudy> getCaseStudyById(Long id) {
        return caseStudyRepository.findById(id);
    }

    public CaseStudy findCaseStudyById(Long caseStudyId) {
        return caseStudyRepository.findCaseStudyById(caseStudyId);
    }

    // Save a new case study
    public CaseStudy saveCaseStudy(CaseStudy caseStudy) {
        return caseStudyRepository.save(caseStudy);
    }

    // Update an existing case study
    public CaseStudy updateCaseStudy(Long id, CaseStudy updatedCaseStudy) {
        Optional<CaseStudy> caseStudyOptional = caseStudyRepository.findById(id);
        if (caseStudyOptional.isEmpty()) {
            throw new RuntimeException("Case study not found");
        }

        CaseStudy caseStudy = caseStudyOptional.get();
        caseStudy.setName(updatedCaseStudy.getName());
        caseStudy.setDescription(updatedCaseStudy.getDescription());
        caseStudy.setUsers(updatedCaseStudy.getUsers());
        return caseStudyRepository.save(caseStudy);
    }

    // Delete a case study
    public void deleteCaseStudy(Long id) {
        caseStudyRepository.deleteById(id);
    }

    public CaseStudy createCaseStudy(CaseStudyRequest request, Set<Role> roles, Set<User> users) {
        CaseStudy newCaseStudy = CaseStudy.builder()
                .name(request.getName())
                .description(request.getDescription())
                .users(users == null ? new HashSet<>() : users)
                .roles(roles == null ? new HashSet<>() : roles)  // Assign roles set
                .build();

        return caseStudyRepository.save(newCaseStudy);
    }

    // Find a role by ID for assignment
    public Role findOneRole(Long roleId) throws NotFoundException {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public CaseStudy assignUsersToCaseStudy(Long caseStudyId, List<Long> userIds) throws NotFoundException {
        Optional<CaseStudy> caseStudyOptional = caseStudyRepository.findById(caseStudyId);
        if (caseStudyOptional.isEmpty()) {
            throw new NotFoundException("Case Study not found");
        }

        CaseStudy caseStudy = caseStudyOptional.get();
        List<User> usersToAdd = new ArrayList<>();

        for (Long userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("User with ID " + userId + " not found");
            }
            usersToAdd.add(userOptional.get());
        }

        // Add all users to the case study's set of users
        caseStudy.getUsers().addAll(usersToAdd);

        // Persist the updated case study
        return caseStudyRepository.save(caseStudy);
    }

    public CaseStudy unassignUsersFromCaseStudy(Long caseStudyId, Long userId) throws NotFoundException {
        // Fetch the CaseStudy by ID
        Optional<CaseStudy> caseStudyOptional = caseStudyRepository.findById(caseStudyId);
        if (caseStudyOptional.isEmpty()) {
            throw new NotFoundException("Case Study not found");
        }

        CaseStudy caseStudy = caseStudyOptional.get();

        // Fetch the User by ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }

        User userToRemove = userOptional.get();

        // Check if the user is assigned to the case study
        if (!caseStudy.getUsers().contains(userToRemove)) {
            throw new NotFoundException("User with ID " + userId + " is not assigned to this case study");
        }

        // Remove the user from the case study's set of users
        caseStudy.getUsers().remove(userToRemove);

        // Persist the updated case study
        return caseStudyRepository.save(caseStudy);
    }

    public List<User> getAssignedUsers(Long caseStudyId) throws NotFoundException {
        // Find user IDs assigned to the case study
        List<Long> userIds = caseStudyRepository.findAssignedUserIdsByCaseStudyId(caseStudyId);

        if (userIds.isEmpty()) {
            throw new NotFoundException("No users assigned to this case study");
        }

        // Fetch the User entities based on the user IDs
        return userRepository.findAllById(userIds);
    }

}
