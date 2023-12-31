package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.repository.AdminRepository;
import com.ecommerce.library.repository.RoleRepository;
import com.ecommerce.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Override
	public Admin findByUsername(String username) {
		// TODO Auto-generated method stub
		return adminRepository.findByUsername(username);
	}

	@Override
	public Admin save(AdminDto adminDto) {
		Admin admin = new Admin();
		admin.setFirstName(adminDto.getFirstName());
		admin.setLastName(adminDto.getLastName());
		System.out.println("username =" + adminDto.getUsername());
		System.out.println("pw =" + adminDto.getPassword());
		admin.setUsername(adminDto.getUsername());
		admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));

		admin.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
		// TODO Auto-generated method stub
		return adminRepository.save(admin);
	}

}
