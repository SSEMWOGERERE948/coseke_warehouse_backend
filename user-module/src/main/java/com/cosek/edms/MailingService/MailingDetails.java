package com.cosek.edms.MailingService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailingDetails {
    private String[] recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    private String[] cc;
    private String[] bcc;

}
