package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.repository.OrderDetailRepository;
import com.ecommerce.library.service.OrderDetailService;
import com.ecommerce.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailRepository repository;
    @Override
    public Long getOrderDetailNotReviewed(Customer customer, Product product) {
        return repository.getOrderDetailNotReviewed(customer.getId(), product.getId());
    }

    @Override
    public OrderDetail getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public OrderDetail updateAfterReviewed(Long id) {
        OrderDetail orderDetail = repository.getById(id);
        orderDetail.setReviewed(true);
        return repository.save(orderDetail);
    }
}
