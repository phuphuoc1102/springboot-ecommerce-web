package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.VerificationToken;
import com.ecommerce.library.repository.VerificationTokenRepository;
import com.ecommerce.library.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public VerificationToken findByCustomer(Customer customer) {
        return tokenRepository.findByCustomer(customer);
    }
}
