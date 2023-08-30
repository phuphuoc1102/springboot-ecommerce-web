package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Activity;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.ActivityService;
import com.ecommerce.library.service.AdminService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.StatsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final AdminService adminService;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private ActivityService activityService;
    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }

    @RequestMapping({"/index","/"})
    public String index(Model model, HttpSession session) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        else session.setAttribute("username",authentication.getName());
        LocalDate today = LocalDate.now();
        session.setAttribute("currentMonth",today.getMonthValue());
        session.setAttribute("currentYear",today.getYear());
        model.addAttribute("statsForMonthAndYear",statsService.StatsForMonthAndYear());
        LocalDate date = LocalDate.now();
        Long annualEarning = orderService.annualEarning();
        Long monthlyEarning = orderService.monthlyEarning(date.getMonth().getValue());
        System.out.println(annualEarning + " " + monthlyEarning);
        model.addAttribute("annualEarning",annualEarning);
        model.addAttribute("monthlyEarning",monthlyEarning);
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());

        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model,Principal principal) {

        try {

            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                //adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                String activity = "Add new admin : username = " + adminDto.getUsername();
                Activity activitySave = new Activity();
                activitySave.setActivity(activity);
                activitySave.setAdmin(adminService.findByUsername(principal.getName()));
                activityService.save(activitySave);
                System.out.println("success");
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                System.out.println("password not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "register";

    }
    @GetMapping("/test")
    public String test(){
        return "test";
    }
}