CREATE TABLE case_studies (
                              id INT IDENTITY(1,1) PRIMARY KEY,
                              title NVARCHAR(255) NOT NULL,
                              description NVARCHAR(MAX),
                              department NVARCHAR(100),
                              created_by INT,
                              created_on DATETIME DEFAULT GETDATE(),
                              status NVARCHAR(50) DEFAULT 'New',
                              document_pdf_path NVARCHAR(255),
                              notes NVARCHAR(MAX)
);

-- Inserting dummy data into case_studies table
INSERT INTO case_studies (title, description, department, created_by, created_on, status, document_pdf_path, notes)
VALUES
    ('Pharmacy Stock Management', 'Case study on the management of pharmacy stock and medications.', 'Pharmacy', 1, GETDATE(), 'New', 'C:\\Documents\\Pharmacy_Stock_Management.pdf', 'Important for regulatory compliance'),
    ('Lab Test Analysis', 'Study on the analysis and tracking of lab tests over time.', 'Lab', 2, GETDATE(), 'In Progress', 'C:\\Documents\\Lab_Test_Analysis.pdf', 'Includes historical lab data'),
    ('Regulatory Compliance', 'Documentation of compliance with government regulations.', 'Regulatory', 3, GETDATE(), 'Completed', 'C:\\Documents\\Regulatory_Compliance.pdf', 'Approved by the PI'),
    ('Patient Medical History', 'Case study focusing on archiving patient medical histories.', 'Clinic', 4, GETDATE(), 'Archived', 'C:\\Documents\\Patient_Medical_History.pdf', 'Archived after 5 years of inactivity'),
    ('Pharmacy Incident Report', 'Incident reports related to medication dispensing errors.', 'Pharmacy', 5, GETDATE(), 'New', 'C:\\Documents\\Pharmacy_Incident_Report.pdf', 'Pending review by the pharmacist of records');

CREATE TABLE case_study (
                            id INT IDENTITY(1,1) PRIMARY KEY, -- Auto-incrementing ID
                            name NVARCHAR(255) NOT NULL,      -- Use NVARCHAR for variable-length string data
                            description NVARCHAR(MAX),        -- Use NVARCHAR(MAX) for larger text content
                            enabled BIT                       -- Use BIT for boolean-like columns (0 or 1)
);

CREATE TABLE case_study_permissions (
                                        id INT IDENTITY(1,1) PRIMARY KEY,              -- Auto-incrementing unique identifier
                                        case_study_id INT FOREIGN KEY REFERENCES case_study(id), -- Links to the case study
                                        role_id BIGINT FOREIGN KEY REFERENCES roles(id),  -- Links to the role
                                        permission_id BIGINT FOREIGN KEY REFERENCES permissions(id), -- Links to the permission
                                        checked BIT DEFAULT 0,                         -- Whether this permission is granted (0 or 1)
                                        created_at DATETIME DEFAULT GETDATE(),         -- Timestamp when the permission was added
                                        updated_at DATETIME DEFAULT GETDATE()          -- Timestamp for the last update
);

select * from case_study;
select * from case_study_permissions;
select * from users;
select * from user_role;