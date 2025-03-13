package com.cosek.edms.role.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRequest {
    private Long userId;
    private List<String> userTypes;  // This will hold one or more user types
}
