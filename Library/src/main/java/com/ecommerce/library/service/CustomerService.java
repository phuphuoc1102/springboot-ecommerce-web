package com.ecommerce.library.service;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    CustomerDto save(MultipartFile imageCustomer, CustomerDto customerDto);
    Customer findByUsername(String username);
    Customer update(MultipartFile imageCustomer, Customer customer);
    Customer saveChanges(Customer customer);
}
