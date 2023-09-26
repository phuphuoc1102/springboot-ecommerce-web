package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.PetService;
import com.ecommerce.library.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PetService petService;
    @Autowired
    private CustomerService customerService;

    @GetMapping({"/home", "/"})
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model, HttpSession session) {
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("productDtos", productService.findAll());
        model.addAttribute("trendingProducts", productService.findTrendingProducts());
        model.addAttribute("featuredCategory", categoryService.GetFeaturedCategory());
        model.addAttribute("productByCategory1", productService.findByCategoryId(1L));
        model.addAttribute("productByCategory2", productService.findByCategoryId(2L));
        model.addAttribute("categoriesByPetId1", categoryService.findByPetId(1L));
        model.addAttribute("categoriesByPetId2", categoryService.findByPetId(2L));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(principal == null || (principal instanceof OAuth2AuthenticationToken))) {
            Map<String,Object> attributes =  principal.getAttributes();
            System.out.println("attributes = " + attributes);
            String username = principal.getAttribute("email");
            session.setAttribute("username", username);
            Customer customer = customerService.findByUsername(username);
            if(customer == null) {
                Customer saveCustomer = new Customer();
                String given_name = principal.getAttribute("given_name");
                String family_name = principal.getAttribute("family_name");
                String address = principal.getAttribute("address");
                String phone = principal.getAttribute("phone");
                saveCustomer.setAddress(address);
                saveCustomer.setPhoneNumber(phone);
                saveCustomer.setUsername(username);
                saveCustomer.setLastName(given_name);
                saveCustomer.setFirstName(family_name);
                customerService.saveChanges(saveCustomer);
            }
            else{
                ShoppingCart shoppingCart = customer.getShoppingCart();
                session.setAttribute("shoppingCart", shoppingCart);
            }
            System.out.println("principal get name " + principal.getAttribute("email"));
            return "index";
        }
        System.out.println("principal get name " + authentication.getName());
        Customer customer = customerService.findByUsername(authentication.getName());
        if(customer != null) {
            session.setAttribute("username", customer.getUsername());
            ShoppingCart shoppingCart = customer.getShoppingCart();
            model.addAttribute("shoppingCart", shoppingCart);
        }



        return "index";
    }

    @GetMapping("/social-login")
    public String currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        System.out.println(oAuth2AuthenticationToken.getPrincipal().getAttribute("name").toString());
        System.out.println(oAuth2AuthenticationToken.getPrincipal().getAttribute("email").toString());
        return "nhap";
    }

}
