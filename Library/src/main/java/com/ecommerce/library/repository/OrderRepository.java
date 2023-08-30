package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query(value = "select o.order_id,o.delivery_date,o.order_date,o.order_status,o.notes,o.shipping_fee,o.total_price,o.customer_id from orders o inner join customers c " +
            "on c.customer_id = o.customer_id " +
            "where o.order_status = ?1 and c.username = ?2 ",nativeQuery = true)
    List<Order> findByOrderStatusAndUsername(String orderStatus,String username);
    @Query(value = "select o.order_id,o.delivery_date,o.order_date,o.order_status,o.notes,o.shipping_fee,o.total_price,o.customer_id from orders o inner join customers c " +
            "on c.customer_id = o.customer_id " +
            "where c.username = ?1 ",nativeQuery = true)
    List<Order> findByUsername(String username);

    @Query(value = "select * from orders o where o.order_status=?1 and year(o.order_date)=?2 and month(o.order_date)=?3",nativeQuery = true)
    List<Order> findByStatusAndOrderDate(String orderStatus, String year,String month);
    @Query(value = "select * from orders o where o.order_status=?1 and year(o.order_date)=?2",nativeQuery = true)
    List<Order> findByStatusAndOrderYear(String orderStatus, String year);
    @Query(value = "select * from orders o where year(o.order_date)=?1 and month(o.order_date)=?2",nativeQuery = true)
    List<Order> findByOrderDate(String year, String month);
    @Query(value = "select * from orders o where year(o.order_date)=?1",nativeQuery = true)
    List<Order> findByOrderYear(String year);
    @Query(value = "SELECT  SUM(total_price)  FROM orders",nativeQuery = true)
    Long annualEarning();
    @Query(value = "SELECT  SUM(total_price)  FROM orders where month(order_date) = ?1",nativeQuery = true)
    Long monthlyEarning(int month);

}
