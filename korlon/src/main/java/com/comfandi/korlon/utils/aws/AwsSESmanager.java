package com.comfandi.korlon.utils.aws;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

@Component
public class AwsSESmanager {

    private String smtpUsername;
    private String smtpPassword;
    private final String smtpHost;
    private final int smtpPort;


    public AwsSESmanager( @Value("${spring.mail.username}") String smtpUsername,@Value("${spring.mail.password}") String smtpPassword) {
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.smtpHost = "email-smtp.us-east-1.amazonaws.com"; // or your SES region
        this.smtpPort = 587; // TLS port (or 465 for SSL)
    }

    public void sendEmailWithAttachment(
            String from,
            String to,
            String subject,
            String bodyText,
            File attachment,
            String fileName
    ) throws Exception {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);

        // Body + Attachment
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(bodyText);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);
        attachmentPart.setFileName(fileName);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentPart);


        msg.setContent(multipart);

        Transport transport = session.getTransport();

        try {
            transport.connect(smtpHost, smtpUsername, smtpPassword);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } finally {
            transport.close();
        }
    }

    public void sendEmail(
            String from,
            String to,
            String subject,
            String bodyText
    ) throws Exception {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(bodyText);

        Transport transport = session.getTransport();
        try {
            transport.connect(smtpHost, smtpUsername, smtpPassword);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("✅ Email enviado (sin adjunto)!");
        } finally {
            transport.close();
        }
    }
    

}


