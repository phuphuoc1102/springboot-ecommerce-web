package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.exception.CustomerAlreadyExistsException;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.VerificationToken;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.RoleRepository;
import com.ecommerce.library.repository.VerificationTokenRepository;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.ultils.CustomerImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerImageUpload customerImageUpload;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Override
    public CustomerDto save(MultipartFile imageCustomer,CustomerDto customerDto) {
        try {

            Customer customer = new Customer();
            if(imageCustomer == null){
                customer.setImage(null);
            }
            else {
                customerImageUpload.uploadImage(imageCustomer);
                customer.setImage(Base64.getEncoder().encodeToString(imageCustomer.getBytes()));
            }
            customer.setFirstName(customerDto.getFirstName());
            customer.setLastName(customerDto.getLastName());
            customer.setUsername(customerDto.getUsername());
            customer.setPassword(customerDto.getPassword());
            customer.setPhoneNumber(customerDto.getPhoneNumber());
            customer.setImage(customerDto.getImage());
            customer.setAddress(customerDto.getAddress());
            customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
            Customer customerSave = customerRepository.save(customer);
            return mapperDTO(customerSave);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Customer update(MultipartFile imageCustomer, Customer customer) {
        try {
//            System.out.println(customer.getUsername());
            Customer customerUpdate = customerRepository.findByUsername(customer.getUsername());
//            customerUpdate.setId(customer.getId());
//            customerUpdate.setFirstName(customer.getFirstName());
//            customerUpdate.setLastName(customer.getLastName());
//            customerUpdate.setPassword(customer.getPassword());
//            customerUpdate.setPhoneNumber(customer.getPhoneNumber());
//            customerUpdate.setAddress(customer.getAddress());
//            System.out.println("address= " + customer.getAddress());
//            customerUpdate.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
            if (imageCustomer == null) {
                customer.setImage(customer.getImage());
                System.out.println("here image  null");
            } else {
                if(customerImageUpload.checkExist(imageCustomer)){
                    customerUpdate.setImage(customer.getImage());
                }
                else{
                    customerImageUpload.uploadImage(imageCustomer);
                    customer.setImage(Base64.getEncoder().encodeToString(imageCustomer.getBytes()));
                    System.out.println("here image not null");
                }
            }
            System.out.println("IMAGE=" + customer.getImage());
            Customer customerSave = customerRepository.save(customer);
            System.out.println("customerSave = " + customerSave);
            return customerSave;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Customer saveChanges(Customer customer) {
       // Customer customerSave = customerRepository.findByUsername(customer.getUsername());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        return customerRepository.save(customer);
    }

    private CustomerDto mapperDTO(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setAddress(customerDto.getAddress());
        customer.setImage(customerDto.getImage());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        return customerDto;
    }

    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public Customer registerCustomer(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());
        if(customer != null){
            throw new CustomerAlreadyExistsException("Customer with email "+customerDto.getUsername() + " already exists");
        }
        var newCustomer = new Customer();
        newCustomer.setFirstName(customerDto.getFirstName());
        newCustomer.setLastName(customerDto.getLastName());
        newCustomer.setUsername(customerDto.getUsername());
        newCustomer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        newCustomer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        newCustomer.setAddress(customerDto.getAddress());
        newCustomer.setPhoneNumber(customerDto.getPhoneNumber());
        System.out.println("roles = " + newCustomer.getRoles());
        return customerRepository.save(newCustomer);
    }

    @Override
    public List<Customer> getUsers() {
        return customerRepository.findAll();
    }

    @Override
    public void saveCustomerVerificationToken(Customer theCustomer, String verificationToken) {
        var token = new VerificationToken(verificationToken,theCustomer);
        verificationTokenRepository.save(token);

    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        Customer customer = token.getCustomer();
        Calendar calendar = Calendar.getInstance();
        if(token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            //verificationTokenRepository.delete(token);
            return "Token already expired";
        }
        customer.setEnabled(true);
        customerRepository.save(customer);
        return "Valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        VerificationToken verificationTokenTime = new VerificationToken("");
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpirationTime(verificationTokenTime.getExpirationTime());
        return verificationTokenRepository.save(verificationToken);
    }
}
