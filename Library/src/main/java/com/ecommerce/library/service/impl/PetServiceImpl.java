package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Pet;
import com.ecommerce.library.repository.PetRepository;
import com.ecommerce.library.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetRepository petRepository;
    @Override
    public List<Pet> findAll() {
        return petRepository.findAll();
    }
}
