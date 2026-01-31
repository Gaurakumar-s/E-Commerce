package com.example.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Our Online Shop!");
            message.setText("Dear " + userName + ",\n\n" +
                    "Thank you for registering with us. We're excited to have you as a customer!\n\n" +
                    "Happy Shopping!\n\n" +
                    "Best regards,\n" +
                    "The Shop Team");
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - email sending should not break the registration flow
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void sendOrderConfirmationEmail(String toEmail, String userName, Long orderId, String totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - Order #" + orderId);
            message.setText("Dear " + userName + ",\n\n" +
                    "Thank you for your order!\n\n" +
                    "Order ID: #" + orderId + "\n" +
                    "Total Amount: $" + totalAmount + "\n\n" +
                    "We will process your order and send you a shipping confirmation soon.\n\n" +
                    "Best regards,\n" +
                    "The Shop Team");
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - email sending should not break the order flow
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }
}
