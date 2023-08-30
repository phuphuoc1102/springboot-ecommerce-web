package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.OrderDetailRepository;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Override
    public Order save(ShoppingCart shoppingCart) {
        if(shoppingCart.getTotalPrice() == 0 || shoppingCart.getTotalItems() == 0){
            return null;
        }
        System.out.println("hehe");
        Order order = new Order();
        order.setOrderStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(new Date());
        order.setCustomer(shoppingCart.getCustomer());

        order.setDeliveryDate(null);
        order.setShippingFee(20000);
        order.setTotalPrice(shoppingCart.getTotalPrice() + order.getShippingFee());
        List<OrderDetail>orderDetailList = new ArrayList<>();
        for(CartItem cartItem : shoppingCart.getCartItemSet()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setUnitPrice(cartItem.getProduct().getCostPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice() + 20000);
            orderDetailRepository.save(orderDetail);
            orderDetailList.add(orderDetail);
            cartItemRepository.delete(cartItem);

        }
        order.setOrderDetailList(orderDetailList);
        shoppingCart.setCartItemSet(new HashSet<>());
        shoppingCart.setTotalItems(0);
        shoppingCart.setTotalPrice(0);
        shoppingCartRepository.save(shoppingCart);
        return orderRepository.save(order);

    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public List<Order> findByOrderStatusAndUsername(String orderStatus,String username) {
        return orderRepository.findByOrderStatusAndUsername(orderStatus,username);
    }

    @Override
    public List<Order> findByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    @Override
    public List<Order> findByStatusAndOrderDate(String orderStatus, String year, String month) {
        if(month != null)
        return orderRepository.findByStatusAndOrderDate(orderStatus,year,month);
        else return orderRepository.findByStatusAndOrderYear(orderStatus,year);
    }
    @Override
    public Page<Order> pageOrders(int pageNo,List<Order> orderList) {
        try{
            Pageable pageable = PageRequest.of(pageNo,12);
            //List<ProductDto> products = transfer(productDtoList);
            Page<Order> orderPage = toPage(orderList,pageable);
            return orderPage;
        }catch(IllegalArgumentException e)
        {
            return null;
        }
    }
    private Page toPage(List<Order> orderList, Pageable pageable){
        if(pageable.getOffset()>= orderList.size()){
            return Page.empty();
        }
        int startIndex = (int)pageable.getOffset();
        int endIndex = (pageable.getOffset()+pageable.getPageSize()> orderList.size())
                ? orderList.size():(int)(pageable.getOffset()+pageable.getPageSize());
        List  subList = orderList.subList(startIndex,endIndex);
        return  new PageImpl(subList,pageable,orderList.size());
    }

    @Override
    public List<Order> findByOrderDate(String year, String month) {

        if(month != null) {

            return orderRepository.findByOrderDate(year, month);
        }
        else {

            return orderRepository.findByOrderYear(year);
        }
    }

    @Override
    public Long annualEarning() {
        return orderRepository.annualEarning();
    }

    @Override
    public Long monthlyEarning(int month) {
        return orderRepository.monthlyEarning(month);
    }
}
