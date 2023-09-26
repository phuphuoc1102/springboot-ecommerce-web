package com.ecommerce.library.service;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.VerificationToken;

public interface VerificationTokenService {
    VerificationToken findByToken(String token);
    VerificationToken findByCustomer(Customer customer);
}
