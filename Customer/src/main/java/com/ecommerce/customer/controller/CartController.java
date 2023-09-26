package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.*;
import com.ecommerce.library.service.impl.ShoppingCartServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;

@Controller
public class
CartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PetService petService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/cart")
    public String cart(Principal principal,@AuthenticationPrincipal OAuth2User oAuth2User,Model model) throws NullPointerException{
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        if(principal == null)
            return "redirect:/dang-nhap";
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if(shoppingCart == null){
            shoppingCart = new ShoppingCart();
            shoppingCart.setTotalPrice(0.0);
            shoppingCart.setTotalItems(0);
            model.addAttribute("check","No item is in your cart");

        }

        model.addAttribute("shoppingCart",shoppingCart);
        return "cart";
    }
    @GetMapping(value = "/add-to-cart/{product_id}")
    public String AddToCart(@AuthenticationPrincipal OAuth2User oAuth2User,@PathVariable Long product_id, @RequestParam(value = "quantity",defaultValue = "1",required = false)int quantity, HttpServletRequest request, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        if(principal == null)
            return "redirect:/dang-nhap";
        System.out.println("hear" + product_id);
        Product product = productService.getById(product_id);
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = shoppingCartService.addItemToCart(product,quantity,customer);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "/update-cart",params = "action=update",method = RequestMethod.POST)
    public String updateCart(@AuthenticationPrincipal OAuth2User oAuth2User,@RequestParam("id") Long id,@RequestParam("quantity") int quantity, Model model, Principal principal){
        if(principal == null){
            return "redirect:/login";
        }else{
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
            Product product  = productService.getById(id);
            ShoppingCart shoppingCart = shoppingCartService.updateItemInCart(product,quantity,customer);
            model.addAttribute("shoppingCart",shoppingCart);
            return "redirect:/cart";
        }
    }
    @RequestMapping(value = "/update-cart",params = "action=delete",method = RequestMethod.POST)
    public String deleteItemInCart(@AuthenticationPrincipal OAuth2User oAuth2User,@RequestParam("id") Long id, Model model,Principal principal){
        if(principal == null){
            return "redirect:/login";
        }else{
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
            Product product  = productService.getById(id);
            ShoppingCart shoppingCart = shoppingCartService.deleteItemInCart(product,customer);
            model.addAttribute("shoppingCart",shoppingCart);
            return "redirect:/cart";
        }
    }
}
