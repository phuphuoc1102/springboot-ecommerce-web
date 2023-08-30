package com.ecommerce.library.service;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    Category save(MultipartFile imageProduct,Category category);
    Optional<Category> findById(Long id);
    Category update(MultipartFile imageProduct,Category category);
    void deleteById(Long id);
    void enabledById(Long id);

    List<CategoryDto> findAllByActivatedTrue();
    List<Category> findByPetId(Long id);
    public List<CategoryDto> GetFeaturedCategory();

}
