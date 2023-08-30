package com.ecommerce.library.dto;

import com.ecommerce.library.model.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String image;
    private boolean activated;
    private boolean deleted;
    private Pet pet;
    private String slug;
}