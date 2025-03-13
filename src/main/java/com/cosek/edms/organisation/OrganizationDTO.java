package com.cosek.edms.organisation;

import java.time.LocalDate;

public class OrganizationDTO {

    private String name;
    private LocalDate expiryDate;
    private LocalDate dateAdded;

    public OrganizationDTO() {}

    public OrganizationDTO(String name, LocalDate expiryDate, LocalDate dateAdded) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.dateAdded = dateAdded;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }
}