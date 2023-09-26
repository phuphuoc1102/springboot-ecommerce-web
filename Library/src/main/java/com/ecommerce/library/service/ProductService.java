package com.ecommerce.library.service;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List <ProductDto> findAll();
    Product save(ProductDto productDto);
    Product update(MultipartFile imageProduct,ProductDto productDto);
    void deleteById(Long id);
    void enableById(Long id);
    Product saveNewProduct(MultipartFile imageProduct, ProductDto product);
    Product getById(Long id);

    Page<ProductDto> pageProduct(int pageNo,List<ProductDto> productDtoList);
    Page<ProductDto>searchProducts(int pageNo,String keyword);
    List<ProductDto> findTrendingProducts();
    List<ProductDto> findByCategoryId(Long category_id);
    ProductDto findById(Long id);
    List<ProductDto> findByPetId(Long pet_id);

    List<ProductDto> findAllProductsByCategorySlug(String slug);
    List<ProductDto> findByCategorySlug(String slug,boolean desc);
    public List<ProductDto> findByPetSlug(String slug,boolean desc);
    List<ProductDto> filterHighPrice();
    List<ProductDto> filterLowPrice();

    List<ProductDto> findAllProductsByPetSlug(String slug);

}
