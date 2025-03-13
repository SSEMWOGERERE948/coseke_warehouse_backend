package com.cosek.edms.files.Models;

import lombok.Data;

@Data
public class FileRequest {
    private String PID;
    private int boxNumber;
    private Long archivalBoxId;
    private Long folderId;
}
