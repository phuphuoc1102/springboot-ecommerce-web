package com.ecommerce.admin.controller;

import com.ecommerce.library.model.Activity;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.service.ActivityService;
import com.ecommerce.library.service.AdminService;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ActivityService activityService;
    @GetMapping("/categories")
    public String categories(Model model, Principal principal){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null || authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List <Category> categories = categoryService.findAll();
        model.addAttribute("categories",categories);
        model.addAttribute("size",categories.size());
        model.addAttribute("title","Category");
        model.addAttribute("categoryNew",new Category());
        return "categories";
    }
    @PostMapping("/add-category")
    public String add(Principal principal,@ModelAttribute("categoryNew") Category category, @RequestParam(value="imageCategory") MultipartFile imageCategory,RedirectAttributes redirectAttributes){
        if(principal == null) return "redirect:/login";
        try {
            categoryService.save(imageCategory,category);
            String activity = "Add new category : category_id = " + category.getId();
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success","Added successfully");
        }
        catch (DataIntegrityViolationException e){
            redirectAttributes.addFlashAttribute("failed","Failed to add new category because of duplicate category name  ");

        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("failed","Error server");
        }
        return "redirect:/categories";
    }
    @RequestMapping(value = "/findById", method = {RequestMethod.PUT, RequestMethod.GET})
    @ResponseBody
    public Category findById( Long id,Model model) {
        Category category = categoryService.findById(id).get();
        model.addAttribute("name",category.getName());
        model.addAttribute("categoryNew",category);
        System.out.println(category.getName());
        return category;

    }
    @PostMapping ("/update-category")
    public String update(Principal principal,Model model,Category category, @RequestParam(value="imageCategory") MultipartFile imageCategory,RedirectAttributes redirectAttributes) {
        if(principal == null) return "redirect:/login";
        try {
            categoryService.update(imageCategory,category);
            String activity = "Update category: category_id = " + category.getId();
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error from server or duplicate name of category, please check again!");
        }
        return "redirect:/categories";
    }


    @RequestMapping(value = "/delete-category", method = {RequestMethod.GET, RequestMethod.PUT})
    public String delete(Principal principal,Long id, RedirectAttributes redirectAttributes) {
        if(principal == null) return "redirect:/login";
        try {
            categoryService.deleteById(id);
            String activity = "Delete category : category_id = " + id;
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/enable-category", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enable(Principal principal,Long id, RedirectAttributes redirectAttributes) {
        if(principal == null) return "redirect:/login";
        try {
            categoryService.enabledById(id);
            String activity = "Enable category : category_id " + id;
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Enable successfully");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server");
        }
        return "redirect:/categories";
    }


}
