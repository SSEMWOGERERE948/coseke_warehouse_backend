package com.cosek.edms.user.Models;

import java.util.List;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address;
    private String password;
}
