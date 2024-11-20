package com.weplayWeb.spring.services;


import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.squareup.square.models.Payment;
import com.weplayWeb.spring.Square.EmailResult;
import com.weplayWeb.spring.Square.TokenWrapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender emailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${spring.mail.password}")
    private String password;
    

    
    public EmailResult sendOrderConfirmationEmail(String orderId, Payment payment, TokenWrapper tokenObject, String receiptUrl) {
    	
    	try {

             MimeMessage message = emailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

             helper.setFrom(new InternetAddress(fromEmail, "WePlay"));
             helper.setTo(tokenObject.getBuyerEmailAddress());
             helper.setSubject("WePlay - We received your payment");
             helper.setText(generateEmailBody(orderId, payment, tokenObject, receiptUrl), true);

             emailSender.send(message);
                     
             return new EmailResult("SUCCESS", "Email sent successfully");
             
         } catch (MessagingException e) {
             logger.error("Failed to send confirmation email", e);
             String errorDetail = e.getMessage();
             if (e.getCause() != null) {
                 errorDetail += " Cause: " + e.getCause().getMessage();
             }
             return new EmailResult("FAILURE", "Failed to send email: " + errorDetail);
         } catch (Exception e) {
             logger.error("Unexpected error while sending email", e);
             return new EmailResult("FAILURE", "Unexpected error: " + e.getMessage());
         }
     }
    

    private String generateEmailBody(String orderId, Payment payment, TokenWrapper tokenObject, String receiptUrl) {
       
    	String fullName = tokenObject.getCustomer().getGivenName() + " " + tokenObject.getCustomer().getFamilyName();
    	String phoneNo = tokenObject.getCustomer().getPhoneNumber();
    	
    	StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        
        // Header
        html.append("<div style='text-align: center; margin-bottom: 20px;'>");
        html.append("<img src='https://res.cloudinary.com/dcmz3jqqd/image/upload/v1732134374/__Facebook_Cover_segxpq.jpg' style='max-width: 600px; height:150px'/>");
        html.append("<p style='color: #4878a3;'>Thank you for your purchase!</p>");
        html.append("</div>");
        
        
        // Digital Receipt Button
        if (receiptUrl != null && !receiptUrl.isEmpty()) {
            html.append("<div style='text-align: center; margin: 20px 0;'>");
            html.append("<a href='").append(receiptUrl).append("' ")
                .append("style='display: inline-block; padding: 10px 20px; ")
                .append("background-color: #4878a3; color: white; ")
                .append("text-decoration: none; border-radius: 5px; ")
                .append("font-weight: bold;'>")
                .append("View Digital Receipt</a>");
            html.append("</div>");
        }
        
        // Order Details
        html.append("<div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0;'>");
        html.append("<h3 style='color: #4878a3; margin-top: 0;'>Order Details:</h3>");
        html.append("<p>Name: <strong>").append(fullName).append("</strong></p>");
        html.append("<p>Phone Number: <strong>").append(phoneNo).append("</strong></p>");
        html.append("<p>Order ID: <strong>").append(orderId).append("</strong></p>");
        html.append("<p>Date: <strong>")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")))
            .append("</strong></p>");
        html.append("</div>");
        
        // Items Table
        html.append("<h3 style='color: #4878a3;'>Items Purchased:</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
        html.append("<tr style='background-color: #f8f9fa;'>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; text-align: left;'>Item</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; text-align: right;'>Quantity</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; text-align: right;'>Price</th>");
        html.append("<th style='padding: 10px; border: 1px solid #ddd; text-align: right;'>Total</th>");
        html.append("</tr>");
        
        double totalAmount = 0;
        for (TokenWrapper.Order order : tokenObject.getOrderInfo()) {
            double itemTotal = order.price() * order.quantity();
            totalAmount += itemTotal;
            
            html.append("<tr>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd;'>")
                .append(order.sectionName()).append("</td>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>")
                .append(order.quantity()).append("</td>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>$")
                .append(String.format("%.2f", order.price())).append("</td>");
            html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'>$")
                .append(String.format("%.2f", itemTotal)).append("</td>");
            html.append("</tr>");
        }
        
        // Total
        html.append("<tr style='background-color: #f8f9fa;'>");
        html.append("<td colspan='3' style='padding: 10px; border: 1px solid #ddd; text-align: right;'><strong>Total Amount:</strong></td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd; text-align: right;'><strong>$")
            .append(String.format("%.2f", totalAmount)).append("</strong></td>");
        html.append("</tr>");
        html.append("</table>");
        
        // Instructions
        html.append("<div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0;'>");
        html.append("<h3 style='color: #4878a3; margin-top: 0;'>Important Instructions:</h3>");
        html.append("<ul style='margin: 0; padding-left: 20px;'>");
        html.append("<li>Please save this email for your records</li>");
        html.append("<li>Present your Order Details at check-in</li>");
        html.append("<li>Valid for one-time admission</li>");
        html.append("</ul>");
        html.append("</div>");
        
        // Footer
        html.append("<div style='background-color: #4878a3; color: white; padding: 20px; margin-top: 30px; border-radius: 5px;'>");
        html.append("<div style='text-align: center; margin-bottom: 15px;'>");
        html.append("<h3 style='color: white; margin: 0 0 10px 0;'>WePlay Indoor Playground</h3>");
        html.append("<p style='margin: 5px 0;'>23131 Ecorse Rd, Taylor MI 48180</p>");
        html.append("</div>");
        
        html.append("<div style='text-align: center;'>");
        html.append("<p style='margin: 5px 0;'>Connect with us:</p>");
        html.append("<a href='https://www.instagram.com/weplay_playground/' ")
            .append("style='color: white; text-decoration: none; margin: 0 10px;'>")
            .append("Instagram</a> | ");
        html.append("<a href='https://www.weplayofficial.com' ")
            .append("style='color: white; text-decoration: none; margin: 0 10px;'>")
            .append("Website</a>");
        html.append("</div>");
        
        // Copyright line
        html.append("<div style='text-align: center; margin-top: 15px; font-size: 12px;'>");
        html.append("© ").append(Year.now().getValue()).append(" WePlay Indoor Playground. All rights reserved.");
        html.append("</div>");
        html.append("</div>");

        
        
        html.append("</div>");
        html.append("</body></html>");
        
        return html.toString();
    }
}
