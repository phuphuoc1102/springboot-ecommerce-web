package com.ecommerce.customer.event.listener;

import com.ecommerce.customer.event.RegistrationCompleteEvent;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CustomerService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
@Slf4j
@Component


public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private  CustomerService customerService;
    @Autowired
    private JavaMailSender mailSender;

    private Customer theCustomer;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //1. Get the newly register customer

        theCustomer = event.getCustomer();
        //2. Create a verification token for the customer
        String verificationToken = UUID.randomUUID().toString();
        //3. Save the verification token for the customer
        customerService.saveCustomerVerificationToken(theCustomer,verificationToken);
        //4. Build the verification url to be sent to the customer
        String url = event.getApplicationUrl()+"/register/verifyYourEmail?token="+verificationToken;
        //5. Sent the email
        try {
            sendVerificationEmai(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration : {}",url);
    }
    public void sendVerificationEmai(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Registration for Petshop";
        String mailContent = "<p> Hi, "+ theCustomer.getFirstName()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Customer Registration Petsop";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("dachai611@gmail.com", senderName);
        messageHelper.setTo(theCustomer.getUsername());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
