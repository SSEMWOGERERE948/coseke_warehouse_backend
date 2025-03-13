package com.cosek.edms.filecategory.Modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCategoryRequest {
    private String name;
    private String description;
}
