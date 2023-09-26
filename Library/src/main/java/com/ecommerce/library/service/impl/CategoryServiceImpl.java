package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.repository.CategoryRepository;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.ultils.CategoryImageUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryImageUpload categoryImageUpload;

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Override
    public Category save(MultipartFile imageCategory, Category category) {

        Category categorySave = new Category(category.getName());
        try {
            if (imageCategory == null) {
                categorySave.setImage(null);
            } else {
                categoryImageUpload.uploadImage(imageCategory);
                categorySave.setImage(Base64.getEncoder().encodeToString(imageCategory.getBytes()));
            }

            return repository.save(categorySave);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Category update(MultipartFile imageCategory, Category category) {
        Category categoryUpdate = null;
        try {
            categoryUpdate = repository.findById(category.getId()).get();
            if (imageCategory == null) {
                categoryUpdate.setImage(categoryUpdate.getImage());
            } else {
                if (categoryImageUpload.checkExist(imageCategory)) {
                    categoryUpdate.setImage(categoryUpdate.getImage());
                } else {
                    categoryImageUpload.uploadImage(imageCategory);
                    categoryUpdate.setImage(Base64.getEncoder().encodeToString(imageCategory.getBytes()));
                }
                categoryUpdate.setName(category.getName());
                categoryUpdate.setActivated(category.isActivated());
                categoryUpdate.setDeleted(category.isDeleted());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return repository.save(categoryUpdate);

    }


    @Override
    public void deleteById(Long id) {
        Category category = repository.getById(id);
        category.setDeleted(true);
        category.setActivated(false);
        repository.save(category);

    }

    @Override
    public void enabledById(Long id) {
        Category category = repository.getById(id);
        category.setDeleted(false);
        category.setActivated(true);
        repository.save(category);
    }

    @Override
    public List<CategoryDto> findAllByActivatedTrue() {
        return transfer(repository.findAllByActivatedTrue());
    }

    @Override
    public List<Category> findByPetId(Long id) {
        return repository.findByPetId(id);
    }
    public List<CategoryDto> GetFeaturedCategory(){
        try {
            List<Category> list = repository.listFeatureCategory();
            return transfer(list);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    private List<CategoryDto> transfer(List<Category>categories){
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for(Category category: categories){
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            categoryDto.setPet(category.getPet());
            categoryDto.setImage(category.getImage());
            categoryDto.setActivated(category.isActivated());
            categoryDto.setDeleted(category.isDeleted());
            categoryDto.setSlug(category.getSlug());
            categoryDtoList.add(categoryDto);
        }
        return  categoryDtoList;
    }


}
