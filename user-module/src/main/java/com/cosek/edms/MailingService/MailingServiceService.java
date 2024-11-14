package com.cosek.edms.MailingService;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@RequiredArgsConstructor
@Service
public class MailingServiceService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(MailingDetails mailingDetails, String from) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(mailingDetails.getRecipient());
            message.setBcc(mailingDetails.getBcc());
            message.setReplyTo(from);
            message.setSubject(mailingDetails.getSubject());
            message.setText(mailingDetails.getMsgBody());
            message.setCc(mailingDetails.getCc());
            javaMailSender.send(message);
    }

    public void sendMailWithAttachment(MailingDetails mailingDetails, String from) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setReplyTo(from);
        mimeMessageHelper.setTo(mailingDetails.getRecipient());
        mimeMessageHelper.setText(mailingDetails.getMsgBody());
        mimeMessageHelper.setSubject(mailingDetails.getSubject());
        FileSystemResource file = new FileSystemResource(new File(mailingDetails.getAttachment()));
        mimeMessageHelper.addAttachment(file.getFilename(), file);
        javaMailSender.send(mimeMessage);
    }

}
