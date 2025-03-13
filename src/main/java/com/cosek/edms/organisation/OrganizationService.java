package com.cosek.edms.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    // Get all organizations
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    // Get a single organization by ID
    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    @Transactional
    public Organization createOrganization(OrganizationDTO orgDTO) {
        // Use the dateAdded from the DTO if provided, otherwise use current date
        LocalDate dateAdded = (orgDTO.getDateAdded() != null)
                ? orgDTO.getDateAdded()
                : LocalDate.now();

        Organization organization = new Organization(
                orgDTO.getName(),
                dateAdded,
                orgDTO.getExpiryDate()
        );
        return organizationRepository.save(organization);
    }

    // Update organization details
    @Transactional
    public Organization updateOrganization(Long id, OrganizationDTO orgDTO) {
        Organization existingOrganization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        existingOrganization.setName(orgDTO.getName());
        existingOrganization.setExpiryDate(orgDTO.getExpiryDate());

        return organizationRepository.save(existingOrganization);
    }

    // Delete organization
    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found");
        }
        organizationRepository.deleteById(id);
    }

    // Extend expiry date
    @Transactional
    public Organization extendExpiryDate(Long id, LocalDate newExpiryDate) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setExpiryDate(newExpiryDate);
        return organizationRepository.save(organization);
    }
}
