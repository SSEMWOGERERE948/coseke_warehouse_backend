package com.cosek.edms.departments.DepartmentModule;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DepartmentUserAssign {
    private Long id;
    private List<String> departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
