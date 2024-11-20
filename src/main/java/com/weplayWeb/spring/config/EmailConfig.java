package com.weplayWeb.spring.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;

@Configuration

public class EmailConfig {
	
	 private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);
	 
	 @Value("${spring.mail.host:smtp.office365.com}")
	    private String host;
	    
	    @Value("${spring.mail.port:587}")
	    private Integer port;
	    
	    @Value("${spring.mail.username:contactus@weplayofficial.com}")
	    private String username;
	    
	    @Value("${spring.mail.password}")
	    private String password;

    @Bean
    JavaMailSender javaMailSender() {
	    	
    	 logger.info("Configuring mail sender with GoDaddy SMTP");
    	 
	        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	        // Set default values if properties are not found
	        mailSender.setHost(host != null ? host : "smtp.office365.com");
	        mailSender.setPort(port != null ? port : 587);
	        mailSender.setUsername(username != null ? username : "contactus@weplayofficial.com");
	        mailSender.setPassword(password);
	        
	        Properties props = mailSender.getJavaMailProperties();
	        props.put("mail.transport.protocol", "smtp");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.starttls.required", "true");
	        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	        props.put("mail.smtp.ssl.trust", "smtp.office365.com");
	        props.put("mail.debug", "true");
	        
	        logger.info("Mail configuration:");
	        logger.info("Host: {}", mailSender.getHost());
	        logger.info("Port: {}", mailSender.getPort());
	        logger.info("Username: {}", mailSender.getUsername());
	        
	        return mailSender;
	    }
    
	    @PostConstruct
	    public void verifyConfiguration() {
	        logger.info("Verifying email configuration:");
	        logger.info("Host: {}", host);
	        logger.info("Port: {}", port);
	        logger.info("Username: {}", username);
	        logger.info("Password present: {}", password != null && !password.isEmpty());
        
        // Test the connection during startup
	     //   testConnection();
    }
	    
	    private void testConnection() {
	        Properties props = new Properties();
	        props.put("mail.transport.protocol", "smtp");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.starttls.required", "true");
	        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	        props.put("mail.smtp.ssl.trust", "smtp.office365.com");
	        
	        Session session = Session.getInstance(props);
	        session.setDebug(true);
	        
	        try (Transport transport = session.getTransport("smtp")) {
	            logger.info("Attempting to connect to SMTP server...");
	            transport.connect(host, port, username, password);
	            logger.info("Successfully connected to SMTP server");
	        } catch (MessagingException e) {
	            logger.error("Failed to test SMTP connection", e);
	            logger.error("Error details: {}", e.getMessage());
	            // Optionally throw an exception if you want to fail application startup
	            // throw new RuntimeException("Failed to establish SMTP connection", e);
	        }
	    }
    
}
