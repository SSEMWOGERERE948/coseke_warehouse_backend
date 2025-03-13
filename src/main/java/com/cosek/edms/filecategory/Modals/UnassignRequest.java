package com.cosek.edms.filecategory.Modals;

import lombok.Data;

import java.util.List;

@Data
public class UnassignRequest {
    private Long fileCategoryId;
    private Long userId;
}
