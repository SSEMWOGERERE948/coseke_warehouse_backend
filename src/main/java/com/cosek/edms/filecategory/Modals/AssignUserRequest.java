package com.cosek.edms.filecategory.Modals;

import lombok.Data;

import java.util.List;

@Data
public class AssignUserRequest {
    private Long fileCategoryId;
    private List<Long> userId;
}
