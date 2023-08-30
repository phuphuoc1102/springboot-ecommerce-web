package com.ecommerce.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    @Size(min = 3, max = 10, message="Invalid first name (3-10 characters)")
    private String firstName;
    @Size(min = 3, max = 10, message="Invalid last name (3-10 characters)")
    private String lastName;
    private String username;
    @Size(min = 5, max = 20, message="Invalid password (5-20 characters)")
    private String password;
    private String repeatPassword;
    private String phoneNumber;
    private String address;
    private String image;
}
