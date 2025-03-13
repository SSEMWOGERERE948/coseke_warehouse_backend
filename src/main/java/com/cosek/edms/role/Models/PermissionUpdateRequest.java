package com.cosek.edms.role.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionUpdateRequest {
    private Long roleId;
    private Long permissionId;
}

