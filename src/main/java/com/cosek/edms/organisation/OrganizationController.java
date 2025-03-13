package com.cosek.edms.organisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/organizations")
@CrossOrigin(origins = "*") // Allows frontend calls from different domains
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    // Get all organizations
    @GetMapping("/all")
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    // Get organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long id) {
        Optional<Organization> organization = organizationService.getOrganizationById(id);
        return organization.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new organization
    @PostMapping("/create")
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationDTO orgDTO) {
        Organization createdOrganization = organizationService.createOrganization(orgDTO);
        return ResponseEntity.ok(createdOrganization);
    }

    // Update organization
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
            @PathVariable Long id,
            @RequestBody OrganizationDTO orgDTO) {
        Organization updatedOrganization = organizationService.updateOrganization(id, orgDTO);
        return ResponseEntity.ok(updatedOrganization);
    }

    // Delete organization
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    // Extend expiry date
    @PatchMapping("/{id}/extend-expiry")
    public ResponseEntity<Organization> extendExpiryDate(
            @PathVariable Long id,
            @RequestParam("newExpiryDate") String newExpiryDate) {

        LocalDate parsedExpiryDate = LocalDate.parse(newExpiryDate);
        Organization updatedOrganization = organizationService.extendExpiryDate(id, parsedExpiryDate);

        return ResponseEntity.ok(updatedOrganization);
    }
}

