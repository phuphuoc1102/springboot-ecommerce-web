package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AccountController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PetService petService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String Profile(HttpSession session, HttpSession request, Model model, Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        Customer customer = customerService.findByUsername(username);

        if (customer != null) {
            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
            model.addAttribute("pets", petService.findAll());
            model.addAttribute("personalInfo", customer);
            model.addAttribute("page", "personalInfopage");
            request.getAttribute("changeStatus");
            session.setAttribute("LoginInfo", customer);
            return "profile";
        } else return "redirect:/dang-nhap";
    }

    @PostMapping("/cap-nhat-thong-tin-khach-hang/{username}")
    public String update(@ModelAttribute("personalInfo") Customer customer, Model model, @PathVariable String username, @RequestParam(value = "imageCustomer",required = false) MultipartFile imageCustomer, RedirectAttributes redirectAttributes) {

        try {
            customerService.update(imageCustomer, customer);
            model.addAttribute("personalInfo", customer);
            model.addAttribute("page", "personalInfopage");
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error from server, please check again!");
        }
        return "redirect:/profile";
    }

    @GetMapping("/tai-khoan-va-bao-mat")
    public String account(@AuthenticationPrincipal OAuth2User oAuth2User,HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        Customer customer = customerService.findByUsername(username);
        if (customer != null) {
            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
            model.addAttribute("pets", petService.findAll());
            model.addAttribute("personalInfo", customer);
            model.addAttribute("page", "accountpage");
            session.setAttribute("LoginInfo", customer);
            return "profile";
        } else return "redirect:/dang-nhap";
    }

    @PostMapping("/doi-mat-khau")
    public String changePassword(@AuthenticationPrincipal OAuth2User oAuth2User,Principal principal, Model model, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        Customer customer = customerService.findByUsername(username);
        model.addAttribute("page", "accountpage");
        model.addAttribute("personalInfo", customer);

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            System.out.println("oldPass" + oldPassword + "newPass" + customer.getPassword());
            model.addAttribute("statusPassword", "Mật khẩu cũ không khớp");
            System.out.println("sai mk");
            return "profile";
        } else {
            if (newPassword.length() > 20 || newPassword.length() < 5) {
                model.addAttribute("passwordError", "Invalid password (5-20 characters)");
                return "profile";
            }
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.saveChanges(customer);
            System.out.println("đúng mk");
            model.addAttribute("changePasswordSuccess", "Đổi mật khẩu thành công");
            return "profile";
        }


    }

}
