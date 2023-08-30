package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.ultils.ProductImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductRepository repository;

    @Override
    public ProductDto findById(Long id) {
        Product product = repository.findById(id).get();
        List <Product> productList = new ArrayList<>();
        productList.add(product);
        List<ProductDto> productDto = transfer(productList);
        return productDto.get(0);
    }



    @Autowired
    private ProductImageUpload productImageUpload;
    @Override
    public List<ProductDto> findAll() {
        List<Product> productList = repository.findAll();
        List<ProductDto> productDtoList = transfer(productList);

        return productDtoList;
    }

    @Override
    public Product save(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setCategory(productDto.getCategory());
        product.setImage(productDto.getImage());
        product.setSalePrice(productDto.getSalePrice());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.set_activated(true);
        product.set_deleted(false);
        return repository.save(product);
    }


    @Override
    public Product saveNewProduct(MultipartFile imageProduct, ProductDto productDto) {
        Product product = new Product();
        try {
            if (imageProduct == null) {
                product.setImage(null);
            } else {
                productImageUpload.uploadImage(imageProduct);
                product.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
            }
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setCostPrice(productDto.getCostPrice());
            product.setCategory(productDto.getCategory());
            product.set_deleted(false);
            product.set_activated(true);
            return repository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<ProductDto> searchProducts(int pageNo,String keyword) {
        Pageable pageable = PageRequest.of(pageNo,10);
        List<ProductDto> productDtoList = transfer(repository.searchProductsList(keyword));
        Page<ProductDto> products = toPage(productDtoList,pageable);
        return products;
    }

    @Override
    public List<ProductDto> findTrendingProducts() {
        List<Product> productList = repository.findTrendingProducts();
        List<ProductDto> productDtoList = transfer(productList);
        return productDtoList;
    }

    @Override
    public Product update(MultipartFile imageProduct, ProductDto productDto) {
        try {
            Product productUpdate = repository.getById(productDto.getId());
            if (imageProduct == null) {
                productUpdate.setImage(productUpdate.getImage());
            } else {
                if (productImageUpload.checkExist(imageProduct)) {
                    productUpdate.setImage(productUpdate.getImage());
                } else {
                    productImageUpload.uploadImage(imageProduct);
                    productUpdate.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
                }
            }
            productUpdate.setCategory(productDto.getCategory());
            productUpdate.setId(productUpdate.getId());
            productUpdate.setName(productDto.getName());
            productUpdate.setDescription(productDto.getDescription());
            productUpdate.setCostPrice(productDto.getCostPrice());
            productUpdate.setSalePrice(productDto.getSalePrice());
            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
            return repository.save(productUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void enableById(Long id) {
        Product product = repository.getById(id);
        product.set_activated(true);
        product.set_deleted(false);
        repository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = repository.getById(id);
        product.set_deleted(true);
        product.set_activated(false);
        repository.save(product);
    }
    @Override
    public Product getById(Long id) {

        Product product = repository.getById(id);
//        productDto.setId(product.getId());
//        productDto.setName(product.getName());
//        productDto.setDescription(product.getDescription());
//        productDto.setCostPrice(product.getCostPrice());
//        productDto.setSalePrice(product.getSalePrice());
//        productDto.setCurrentQuantity(product.getCurrentQuantity());
//        productDto.setCategory(product.getCategory());
//        productDto.setImage(product.getImage());
        return product;
    }

    @Override
    public Page<ProductDto> pageProduct(int pageNo,List<ProductDto> productDtoList) {
        Pageable pageable = PageRequest.of(pageNo,12);
        //List<ProductDto> products = transfer(productDtoList);
        Page<ProductDto> productPage = toPage(productDtoList,pageable);
        return productPage;
    }
    private Page toPage(List<ProductDto> productList, Pageable pageable){
        if(pageable.getOffset()>= productList.size()){
            return Page.empty();
        }
        int startIndex = (int)pageable.getOffset();
        int endIndex = (pageable.getOffset()+pageable.getPageSize()> productList.size())
        ? productList.size():(int)(pageable.getOffset()+pageable.getPageSize());
        List  subList = productList.subList(startIndex,endIndex);
        return  new PageImpl(subList,pageable,productList.size());
    }
    private List<ProductDto> transfer(List<Product>products){
        List<ProductDto> productDtoList = new ArrayList<>();
        for(Product product: products){
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCategory(product.getCategory());
            productDto.setImage(product.getImage());
            productDto.set_activated(product.is_activated());
            productDto.set_deleted(product.is_deleted());
            productDtoList.add(productDto);
        }
        return  productDtoList;
    }

    @Override
    public List<ProductDto> findByCategorySlug(String slug,boolean desc) {
        return desc? transfer(repository.findByCategorySlugDesc(slug)) : transfer(repository.findByCategorySlugAsc(slug));
    }

    @Override
    public List<ProductDto> findByCategoryId(Long category_id) {
        return transfer(repository.findByCategoryId(category_id));
    }
    @Override
    public List<ProductDto> findByPetId(Long pet_id) {
        return transfer(repository.findByPetId(pet_id));
    }
    @Override
    public List<ProductDto> findByPetSlug(String slug,boolean desc) {
        return desc ? transfer(repository.findByPetSlugDesc(slug)) : transfer(repository.findByPetSlugAsc(slug));
    }

    @Override
    public List<ProductDto> filterHighPrice() {
        return transfer(repository.filterHighPrice());
    }

    @Override
    public List<ProductDto> filterLowPrice() {
        return transfer(repository.filterLowPrice());
    }

    @Override
    public List<ProductDto> findAllProductsByPetSlug(String slug) {
        return transfer(repository.findAllProductsByPetSlug(slug));
    }

    @Override
    public List<ProductDto> findAllProductsByCategorySlug(String slug) {
        return transfer(repository.findAllProductsByCategorySlug(slug));
    }
}
