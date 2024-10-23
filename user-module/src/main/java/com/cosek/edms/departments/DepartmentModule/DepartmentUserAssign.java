package com.cosek.edms.departments.DepartmentModule;

import lombok.Data;
import java.util.List;

@Data
public class DepartmentUserAssign {
    private Long userId;
    private List<Long> departmentIds;  // Change to use department IDs
}
