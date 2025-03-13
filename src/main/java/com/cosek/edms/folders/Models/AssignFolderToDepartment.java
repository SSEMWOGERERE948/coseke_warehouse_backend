package com.cosek.edms.folders.Models;

import lombok.Data;

import java.util.List;

@Data
public class AssignFolderToDepartment {
    private List<Long> folderIds;
    private Long departmentId;
    private String operation;
}
