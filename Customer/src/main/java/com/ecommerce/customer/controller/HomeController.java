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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping ({"/home", "/"})
    public String home(Model model, HttpSession session) {
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
        if (authentication == null || (authentication instanceof AnonymousAuthenticationToken))
            System.out.println("name" + authentication.getName());
        else {
            session.setAttribute("username", authentication.getName());
            Customer customer = customerService.findByUsername(authentication.getName());
            ShoppingCart shoppingCart = customer.getShoppingCart();
            model.addAttribute("shoppingCart",shoppingCart);
        }


        return "index";
    }

}
