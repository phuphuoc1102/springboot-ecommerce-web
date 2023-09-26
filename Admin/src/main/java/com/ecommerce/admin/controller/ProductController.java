package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Activity;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.ActivityService;
import com.ecommerce.library.service.AdminService;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private AdminService adminService;
    @GetMapping("/products")
    String products(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<ProductDto> productDtoList = productService.findAll();
        model.addAttribute("title", "Manage Products");
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productDtoList);
        model.addAttribute("size", productDtoList.size());
        model.addAttribute("productDtoNew", new ProductDto());
        return "products";
    }

    @PostMapping("/add-product")
    String addProductsForm(Principal principal,Model model, @ModelAttribute("productDtoNew") ProductDto productDto, RedirectAttributes redirectAttributes) {
//        model.addAttribute("product",new ProductDto());
//        return "add-product";
        if(principal == null) return "redirect:/login";
        try {
            productDto.set_deleted(false);
            productDto.set_activated(true);
            productService.save(productDto);
            String activity = "Add new product : product_id = " + productDto.getId();
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Added new product successfully");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("failed", "Failed to add new product because of duplicate product name  ");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failed", "Error server");
        }
        return "redirect:/products/0";
    }

    @GetMapping("/add-new-product")
    public String addProductPage(Model model) {
        model.addAttribute("title", "Add Product");
        List<CategoryDto> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", new ProductDto());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-new-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct") MultipartFile imageProduct,
                              RedirectAttributes redirectAttributes,Principal principal) {
        if(principal == null) return "redirect:/login";
        try {
            productService.saveNewProduct(imageProduct, product);
            String activity = "Add new product : product_id = " + product.getId();
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Add new product successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
        }
        return "redirect:/products/0";
    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<CategoryDto> categories = categoryService.findAllByActivatedTrue();
        ProductDto productDto = productService.findById(id);
        model.addAttribute("title", "Add Product");
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", productDto);
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") MultipartFile imageProduct,
                                RedirectAttributes redirectAttributes,Principal principal) {
        try {

            productService.update(imageProduct, productDto);
            String activity = "Update product : product_id = " + productDto.getId();
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/enable-product/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enableProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,Principal principal) {
        if(principal == null) return "redirect:/login";
        try {
            productService.enableById(id);
            String activity = "Enable product : product_id = " + id;
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Enable successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to enabled");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/delete-product/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String deletedProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,Principal principal) {
        if(principal == null) return "redirect:/login";
        try {
            productService.deleteById(id);
            String activity = "Delete product : product_id =  " + id;
            Activity activitySave = new Activity();
            activitySave.setActivity(activity);
            activitySave.setAdmin(adminService.findByUsername(principal.getName()));
            activityService.save(activitySave);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
        }
        return "redirect:/products/0";
    }

    @GetMapping("/products/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<ProductDto> productList= productService.findAll();
        Page<ProductDto> products = productService.pageProduct(pageNo,productList);
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "products";
    }
    @GetMapping("/search-products/{pageNo}")
    public String searchProducts(@PathVariable("pageNo") int pageNo,@RequestParam("keyword") String keyword, Model model, Principal principal ){
        if(principal == null)
            return "redirect:/login";
        model.addAttribute("title","Search Result");
        Page<ProductDto> products = productService.searchProducts(pageNo,keyword);
        model.addAttribute("products",products);
        model.addAttribute("size",products.getSize());
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("keyword",keyword);
        model.addAttribute("totalPages",products.getTotalPages());
        return "result-products";

    }
}
