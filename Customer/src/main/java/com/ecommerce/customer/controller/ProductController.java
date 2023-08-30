package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.PetService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PetService petService;

    @GetMapping("/chi-tiet-san-pham")
    public String product1(Model model, @RequestParam("id") Long id) {
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("pets", petService.findAll());
        model.addAttribute("product", productService.findById(id));
        ProductDto productDto = productService.findById(id);
        model.addAttribute("productsByCategory", productService.findByCategoryId(productDto.getCategory().getId()));
        return "product_detail";
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
    public String pageProductByPetId(@PathVariable("slug") String slug,@RequestParam("page") int page, Model model) {
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
    public String shopByPetDesc(Model model,@RequestParam("page") int page,@PathVariable("slug") String slug) {
        List<ProductDto> productDtoList = productService.findByPetSlug(slug,true);
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
    public String shopByPetAsc(Model model,@RequestParam("page") int page,@PathVariable("slug") String slug) {
        List<ProductDto> productDtoList = productService.findByPetSlug(slug,false);
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
        List<ProductDto> productDtoList = productService.findByCategorySlug(slug,true);
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
        List<ProductDto> productDtoList = productService.findByCategorySlug(slug,false);
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
