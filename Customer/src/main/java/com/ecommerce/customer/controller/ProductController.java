package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.Review;
import com.ecommerce.library.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PetService petService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private  OrderDetailService orderDetailService;

    @GetMapping("/chi-tiet-san-pham")
    public String product1(Principal principal, Model model, @RequestParam("id") Long id, @AuthenticationPrincipal OAuth2User oAuth2User) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (oAuth2User != null) {
            username = oAuth2User.getAttribute("email");
            System.out.println("email = " + username);
        } else {
            username = authentication.getName();
            System.out.println("emailprincipal = " + username);
        }
        if ( principal != null) {
            Customer customer = customerService.findByUsername(username);
            System.out.println("usernameee = " + username);
            model.addAttribute("review",new Review());
            List<Long> isReviewed = orderService.isReviewed(customer,id);
            model.addAttribute("isReviewed",isReviewed);
        }
        Product product = productService.getById(id);
        if(product != null) {
            if(reviewService.getReviewCount(product) > 0) {
                model.addAttribute("intAvgRating", (int)reviewService.getAvgRating(product));
                model.addAttribute("avgRating", reviewService.getAvgRating(product));
            }
            model.addAttribute("reviewsCount", reviewService.getReviewCount(product));
            model.addAttribute("reviews", reviewService.getReviewByProduct(product));
        }
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("product", productService.findById(id));
        ProductDto productDto = productService.findById(id);
        model.addAttribute("productsByCategory", productService.findByCategoryId(productDto.getCategory().getId()));
        return "product_detail";
    }
    @PostMapping("/danh-gia-san-pham")
    public String rating(Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User, @RequestParam("id")Long id, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("review") Review review){
        if(principal == null){
            return "redirect:/dang-nhap";
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
        Product product = productService.getById(id);
        Customer customer = customerService.findByUsername(username);
        review.setProduct(product);
        review.setCustomer(customer);
        review.setCreatedAt(new Date());
        reviewService.save(review);
        Long order_detail_id = orderDetailService.getOrderDetailNotReviewed(customer,product);
        orderDetailService.updateAfterReviewed(order_detail_id);
        redirectAttributes.addFlashAttribute("status","Bạn đã đánh giá sản phẩm thành công.");
        return "redirect:/chi-tiet-san-pham?id=" + id;
    }

    @GetMapping("/the-loai/{slug}")
    public String shopByCategory(Model model, @PathVariable("slug") String slug, @RequestParam("page") int page) {
        System.out.println(slug);
        List<ProductDto> productDtoList = productService.findAllProductsByCategorySlug(slug);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("productByCategorySlug", products);
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_category";
    }

    //        @GetMapping("/{slug}")
//    public String shopByPet(Model model, @PathVariable("slug") String slug) {
//        System.out.println(slug);
//        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//        model.addAttribute("productByPetSlug", productService.findByPetSlug(slug));
//        model.addAttribute("pets", petService.findAll());
//        //model.addAttribute("productDtos",productService.findByPetId(id));
//        return "shop_by_pet";
//    }
    @GetMapping("/{slug}")
    public String pageProductByPetId(@PathVariable("slug") String slug, @RequestParam("page") int page, Model model) {
        List<ProductDto> productDtoList = productService.findAllProductsByPetSlug(slug);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("productByPetSlug", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        // model.addAttribute("slug", slug);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_pet";
    }

    @GetMapping("/{slug}/filter/desc")
    public String shopByPetDesc(Model model, @RequestParam("page") int page, @PathVariable("slug") String slug) {
        List<ProductDto> productDtoList = productService.findByPetSlug(slug, true);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_pet_desc_price";
    }

    @GetMapping("/{slug}/filter/asc")
    public String shopByPetAsc(Model model, @RequestParam("page") int page, @PathVariable("slug") String slug) {
        List<ProductDto> productDtoList = productService.findByPetSlug(slug, false);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("products", products);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_pet_asc_price";
    }

    @GetMapping("/the-loai/{slug}/filter/desc")
    public String shopByCategoryDesc(Model model, @PathVariable("slug") String slug, @RequestParam("page") int page) {
        System.out.println(slug);
        List<ProductDto> productDtoList = productService.findByCategorySlug(slug, true);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        System.out.println("size = " + products.getSize());
        System.out.println("page = " + products.getTotalPages());
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("productByCategorySlug", products);
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_category_desc_price";
    }

    @GetMapping("/the-loai/{slug}/filter/asc")
    public String shopByCategoryAsc(Model model, @PathVariable("slug") String slug, @RequestParam("page") int page) {
        System.out.println(slug);
        List<ProductDto> productDtoList = productService.findByCategorySlug(slug, false);
        Page<ProductDto> products = productService.pageProduct(page, productDtoList);
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("productByCategorySlug", products);
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop_by_category_asc_price";
    }


}
