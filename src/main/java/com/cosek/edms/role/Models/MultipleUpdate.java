package com.cosek.edms.role.Models;

import com.cosek.edms.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleUpdate {
    private Long roleId;  // Add this field to hold the role ID
    private boolean status; // If you have a status field, e.g., for toggling between add/remove
    private List<Permission> permissions;  // A list of permissions to update
}
