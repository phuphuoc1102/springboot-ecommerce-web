package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public String Profile(HttpSession session, HttpSession request, Model model, Principal principal) {
        String username = principal.getName();
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
    public String update(@ModelAttribute("personalInfo") Customer customer, Model model, @PathVariable String username, @RequestParam(value = "imageCustomer") MultipartFile imageCustomer, RedirectAttributes redirectAttributes) {
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
    public String account(HttpSession session, HttpSession request, Model model, Principal principal) {


        String username = principal.getName();
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
    public String changePassword(Principal principal, Model model, @RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,RedirectAttributes redirectAttributes) {
        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("page","accountpage");
        model.addAttribute("personalInfo",customer);

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            System.out.println("oldPass" + oldPassword + "newPass" + customer.getPassword());
            model.addAttribute("statusPassword", "Mật khẩu cũ không khớp");
            System.out.println("sai mk");
            return "profile";
        }
        else {
            if(newPassword.length()>20 || newPassword.length()<5) {
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
