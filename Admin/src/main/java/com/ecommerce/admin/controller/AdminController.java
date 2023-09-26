package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Activity;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.service.ActivityService;
import com.ecommerce.library.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ActivityService activityService;
    @RequestMapping(value = "/orders/{orderStatus}/{currentPage}", method = RequestMethod.GET)
    public String ManageOrder( Model model, @PathVariable int currentPage,
                              @PathVariable String orderStatus, @RequestParam(name = "month", defaultValue = "null",required = false) String month,
                              @RequestParam(name = "year", defaultValue = "2023",required = false) String year, Principal principal){
        if(principal == null)
            return "redirect:/login";
        List<Order> orderList = new ArrayList<>();
        if(orderStatus.equals("all")){
            orderList= orderService.findByOrderDate(year, month);
        }
        else
            orderList= orderService.findByStatusAndOrderDate(orderStatus, year, month);
        int totalItem = orderList.size();
        Page<Order> orders = orderService.pageOrders(currentPage,orderList);
        try {
            model.addAttribute("size", orders.getSize());
        }catch (NullPointerException e){
            model.addAttribute("size",0);
        }
        model.addAttribute("status",orderStatus);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", orders.getTotalPages());
        if( orders.getSize() == 0){
            model.addAttribute("noOrder","Chưa có đơn hàng");
        }
        return "orders";

    }
    @GetMapping("/activities/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Activity> activityList= activityService.findAll();
        Page<Activity> activityPage = activityService.pageActivities(pageNo,activityList);
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", activityPage.getSize());
        model.addAttribute("activities", activityPage);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", activityPage.getTotalPages());
        return "activities";
    }

}
