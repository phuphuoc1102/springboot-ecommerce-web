package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PetService petService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StatsService statsService;
    @GetMapping("/check-out")
    public String checkout(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        if (customer.getPhoneNumber() == null || customer.getAddress() == null) {
            model.addAttribute("personalInfo", customer);
            model.addAttribute("warning", "You must fill the infomation before checkout");
            model.addAttribute("page", "personalInfopage");
            return "profile";
        } else {
            ShoppingCart shoppingCart = customer.getShoppingCart();
            model.addAttribute("shoppingCart", shoppingCart);
            model.addAttribute("personalInfo", customer);
        }
        return "checkout";
    }

    @PostMapping("/save-order")
    public String saveOrder(Principal principal, Model model, HttpSession session, @ModelAttribute("personalInfo") Customer receiver) throws NullPointerException {
        if (principal == null)
            return "redirect:/dang-nhap";
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        Order orderSave = orderService.save(shoppingCart);
        statsService.updateStats();
        try {
            if(orderSave.getId() == null)
                session.setAttribute("orderId", 0);
        }catch (NullPointerException e){
            return "redirect:/thong-bao";
        }
        session.setAttribute("orderId", orderSave.getId());
        session.setAttribute("receiverInfo", receiver);
        return "redirect:/thong-bao";
    }

    @GetMapping ("/thong-bao")
    public String order(Principal principal,Model model) {
        if (principal == null)
            return "redirect:/dang-nhap";
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        return "ordernotify";
    }

    @GetMapping("/chi-tiet-don-hang/{id}")
    public String saveOrder(HttpSession session, Principal principal, Model model, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        Customer receiver = (Customer) session.getAttribute("receiverInfo");
        model.addAttribute("receiverInfo", receiver);
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        Order order = orderService.findById(id);
        model.addAttribute("Order", order);
        return "order_detail";
    }
    @GetMapping("/don-hang/{status}")
    public String order(Principal principal,HttpSession session, HttpServletRequest request, @PathVariable String status, Model model) {

        Customer customer= customerService.findByUsername(principal.getName());
        if (customer!=null) {
            List<Order> orderList = new ArrayList<>();
            if(status.equals("all")){
                 orderList=orderService.findByUsername(principal.getName());
            }
            else {
                 orderList = orderService.findByOrderStatusAndUsername(status, principal.getName());
            }
            List<Order> dataOrder = customer.getOrderList();
            System.out.println("dtaordersize=" + dataOrder.size());
            model.addAttribute("Order", orderList);
            model.addAttribute("dataOrder", orderList);
            model.addAttribute("customer", customer);
            model.addAttribute("page", "orderpage");
            model.addAttribute("status", status);
            return "profile";
        }
        else return "redirect:/dang-nhap";
    }

}
