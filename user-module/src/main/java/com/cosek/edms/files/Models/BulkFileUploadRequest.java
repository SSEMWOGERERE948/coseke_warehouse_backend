package com.cosek.edms.files.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BulkFileUploadRequest {
    private Long archivalBoxId;
    private Integer boxNumber;
    private Long organizationId; // ✅ Added organization selection


    private List<Map<String, Object>> metadataJson;
}
