package com.ecommerce.library.service;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.Product;

public interface OrderDetailService {
    Long getOrderDetailNotReviewed(Customer customer, Product product);
    OrderDetail getById(Long id);
    OrderDetail updateAfterReviewed(Long id);
}
