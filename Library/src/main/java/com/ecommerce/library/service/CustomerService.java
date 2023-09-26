package com.ecommerce.library.service;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.VerificationToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    CustomerDto save(MultipartFile imageCustomer, CustomerDto customerDto);
    Customer findByUsername(String username);
    Customer update(MultipartFile imageCustomer, Customer customer);
    Customer saveChanges(Customer customer);

    //email verification
    Customer registerCustomer(CustomerDto customerDto);
    List<Customer> getUsers();

    void saveCustomerVerificationToken(Customer theCustomer, String verificationToken);

    String validateToken(String theToken);

    VerificationToken generateNewVerificationToken(String oldToken);
}
