package com.ams.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import org.springframework.beans.factory.annotation.Autowired;
// And this one
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Method for Approval
    public void sendApprovalEmail(String toEmail, String name, String role, String officialId) {
    	final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailService.class);
   
    	logger.info("Preparing to send email...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Registration Approved - Admin IQ");
        
        String body = "Dear " + name + ",\n\n" +
                      "Congratulations! Your registration as a " + role + " has been approved.\n" +
                      "Your official ID is: " + officialId + "\n" +
                      "You can now log in to the portal and access your dashboard.\n\n" +
                      "Regards,\nAdmin Team";
        
        logger.info("Email sent to: {}", toEmail);
        message.setText(body);
        logger.info(body);
        message.setFrom("rravi70947@gmail.com");
        try {
        	mailSender.send(message); // SMTP Handshake
        logger.info("Mail Sent Succefully");
        }
        catch(Exception e)
        {
        	System.err.println("Email failed to send: " + e.getMessage());
        }
    }

    // Method for Rejection
    public void sendRejectionEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Registration Update - Admin IQ");
        
        String body = "Dear " + name + ",\n\n" +
                      "Thank you for your interest in Admin IQ. After reviewing your request, " +
                      "we are unable to approve your account at this time.\n" +
                      "If you believe this is a mistake, please contact the department office.\n\n" +
                      "Regards,\nAdmin Team";
        
        message.setText(body);
        try
        {
        mailSender.send(message);
        }
        catch(Exception e)
        {
           	System.err.println("Email failed to send: " + e.getMessage());
        }
    }
}