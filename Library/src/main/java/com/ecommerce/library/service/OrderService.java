package com.ecommerce.library.service;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Order save(ShoppingCart shoppingCart);
    Order findById(Long id);
    List<Order> findByOrderStatusAndUsername(String orderStatus,String username);
    List<Order> findByUsername(String username);
    List<Order> findByStatusAndOrderDate(String orderStatus, String year,String month);
    List<Order> findByOrderDate( String year,String month);
    Page<Order> pageOrders(int pageNo, List<Order> orderList);
    Long annualEarning();
    Long monthlyEarning(int month);
    void canceledOrder(Long id);
    void reOrder(Long id);
    List<Order> getOrderByCustomer(Customer customer);
    List<Long> isReviewed(Customer customer,Long product_id);

}
