package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.customer.event.RegistrationCompleteEvent;
import com.ecommerce.customer.event.listener.RegistrationCompleteEventListener;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.VerificationToken;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.PetService;
import com.ecommerce.library.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
public class AuthenticationController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PetService petService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private VerificationTokenService tokenService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private RegistrationCompleteEventListener eventListener;
    @Autowired
    private HttpServletRequest servletRequest;
    @RequestMapping(value = "/dang-nhap", method = RequestMethod.GET)
    public String login(Model model, @RequestParam(value = "error", required = false) boolean error) {
        if (error) {
            model.addAttribute("status", "Tên đăng nhập hoặc mật khẩu không chính xác");
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        return "login";
    }


    @GetMapping("/dang-ky")
    public String register(Model model) {
        model.addAttribute("customerDto", new CustomerDto());
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        return "register";
    }

    @PostMapping("/do-register")
    public String registerCustomer(@RequestParam(value = "imageCustomer", required = false) MultipartFile imageCustomer, @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult result,
                                   Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
            String username = customerDto.getUsername();
            Customer customer = customerService.findByUsername(username);
            if (customer != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("username", "Email has been register!");
                return "register";
            }
            if (customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                customerService.save(imageCustomer, customerDto);
                model.addAttribute("success", "Register successfully!");
            } else {
                model.addAttribute("password", "Password is not same");
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");
        }
        return "register";
    }

    @PostMapping(value = "/register")
    public String registerCustomer(@RequestParam(value = "imageCustomer", required = false) MultipartFile imageCustomer, @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult result,
                                   Model model, final HttpServletRequest request) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
            String username = customerDto.getUsername();
            Customer customerCheck = customerService.findByUsername(username);
            if (customerCheck != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("username", "Email has been register!");
                return "register";
            }
            if (customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
                Customer customer = customerService.registerCustomer(customerDto);
                applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(customer, applicationUrl(request)));

                System.out.println("Sucess! Please check your email for your registration");
                model.addAttribute("success", "Register successfully!");
                return "redirect:/verify";
            } else {
                model.addAttribute("password", "Password is not same");
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");


        }
        return "redirect:/verify";
    }
    @GetMapping("/resend-verification-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        VerificationToken verificationToken = customerService.generateNewVerificationToken(oldToken);
        Customer theCustomer = verificationToken.getCustomer();
        resendVerificationTokenEmail(theCustomer, applicationUrl(request), verificationToken);
        return "redirect:/verify";
    }

    private void resendVerificationTokenEmail(Customer theCustomer, String applicationUrl, VerificationToken verificationToken) throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/register/verifyYourEmail?token=" + verificationToken.getToken();
        eventListener.sendVerificationEmai(url);
        System.out.println("Click the link to verify your registration" + url);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping("/register/verifyYourEmail")
    public String verifyEmail(@RequestParam("token") String token,Model model) {
        VerificationToken theToken = tokenService.findByToken(token);
        if (theToken.getCustomer().isEnabled()) {
            System.out.println("This account has been verified, pls login");
            return "redirect:/dang-nhap";
        }
        String verificationResult = customerService.validateToken(token);
        System.out.println(verificationResult);
        if (verificationResult.equalsIgnoreCase("valid")) {
            return "redirect:/verifySuccessfully";
        }
        model.addAttribute("token",token);
        return "resendVerificationToken";
    }

    @GetMapping("/verify")
    public String verify() {
        return "verification";
    }

    @GetMapping("/verifySuccessfully")
    public String verifySuccessfully() {
        return "verifySuccessfully";
    }

}
