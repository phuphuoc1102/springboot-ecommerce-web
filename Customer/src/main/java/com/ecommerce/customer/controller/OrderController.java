package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Activity;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String checkout(@AuthenticationPrincipal OAuth2User oAuth2User,Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
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
    public String saveOrder(@AuthenticationPrincipal OAuth2User oAuth2User,Principal principal, Model model, HttpSession session, @ModelAttribute("personalInfo") Customer receiver) throws NullPointerException {
        if (principal == null)
            return "redirect:/dang-nhap";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
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
    public String order(@AuthenticationPrincipal OAuth2User oAuth2User,Principal principal,Model model) {
        if (principal == null)
            return "redirect:/dang-nhap";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        return "ordernotify";
    }

    @GetMapping("/chi-tiet-don-hang/{id}")
    public String saveOrder(@AuthenticationPrincipal OAuth2User oAuth2User,HttpSession session, Principal principal, Model model, @PathVariable Long id) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        Customer receiver = (Customer) session.getAttribute("receiverInfo");
        model.addAttribute("receiverInfo", receiver);
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
        ShoppingCart shoppingCart = customer.getShoppingCart();
        Order order = orderService.findById(id);
        model.addAttribute("Order", order);
        return "order_detail";
    }
    @GetMapping("/don-hang/{status}")
    public String order(@AuthenticationPrincipal OAuth2User oAuth2User,@PathVariable String status, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        Customer customer= customerService.findByUsername(username);
        if (customer!=null) {
            List<Order> orderList = new ArrayList<>();
            if(status.equals("all")){
                 orderList=orderService.findByUsername(username);
            }
            else {
                 orderList = orderService.findByOrderStatusAndUsername(status, username);
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
    @RequestMapping(value = "/huy-don-hang", method = {RequestMethod.GET, RequestMethod.PUT})
    public String canceledOrder(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("idorder = " + id);
            orderService.canceledOrder(id);
            redirectAttributes.addFlashAttribute("canceledOrder",1);
//            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server");
        }
        return "redirect:/chi-tiet-don-hang/" + id;
    }
    @RequestMapping(value = "/dat-lai", method = {RequestMethod.GET, RequestMethod.PUT})
    public String reOrder(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("idorder = " + id);
            orderService.reOrder(id);
            redirectAttributes.addFlashAttribute("reOrderStatus",1);
//            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server");
        }
        return "redirect:/chi-tiet-don-hang/" + id;
    }

}
