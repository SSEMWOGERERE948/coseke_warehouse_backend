package com.cosek.edms.MailingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MailingServiceService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailingServiceService(@Autowired(required = false) JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(MailingDetails mailingDetails, String from) {
        if (javaMailSender == null) {
            System.out.println("Email service is not configured. Skipping email.");
            return;
        }

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
        if (javaMailSender == null) {
            System.out.println("Email service is not configured. Skipping email with attachment.");
            return;
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setReplyTo(from);
        mimeMessageHelper.setTo(mailingDetails.getRecipient());
        mimeMessageHelper.setText(mailingDetails.getMsgBody());
        mimeMessageHelper.setSubject(mailingDetails.getSubject());

        File attachmentFile = new File(mailingDetails.getAttachment());
        if (attachmentFile.exists()) {
            FileSystemResource file = new FileSystemResource(attachmentFile);
            mimeMessageHelper.addAttachment(file.getFilename(), file);
        } else {
            System.out.println("Attachment file not found: " + mailingDetails.getAttachment());
        }

        javaMailSender.send(mimeMessage);
    }
}
