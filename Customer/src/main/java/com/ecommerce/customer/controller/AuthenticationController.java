package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthenticationController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PetService petService;
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CustomerService customerService;
    @RequestMapping(value = "/dang-nhap",method = RequestMethod.GET)
    public String login(Model model,@RequestParam(value = "error",required = false)boolean error){
        if(error){
            model.addAttribute("status","Tên đăng nhập hoặc mật khẩu không chính xác");
        }
        model.addAttribute("categories",categoryService.findAllByActivatedTrue());
        model.addAttribute("pets",petService.findAll());
        return "login";
    }

    @GetMapping("/dang-ky")
    public String register(Model model){
        model.addAttribute("customerDto", new CustomerDto());
        model.addAttribute("categories",categoryService.findAllByActivatedTrue());
        model.addAttribute("pets",petService.findAll());
        return "register";
    }
    @PostMapping("/do-register")
    public String registerCustomer(@RequestParam(value = "imageCustomer",required = false)MultipartFile imageCustomer,@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
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
                customerService.save(imageCustomer,customerDto);
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

}
