package com.agri.agri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@agricontrol.com");
        message.setTo(toEmail);
        message.setSubject("Verify your AgriControl Account");
        message.setText("Welcome! Your 6-digit verification code is: " + code);

        mailSender.send(message);
    }
}