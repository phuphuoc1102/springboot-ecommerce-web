package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    @Query(value = "SELECT  order_detail_id FROM orders AS o JOIN order_detail AS od ON o.order_id = od.order_id  WHERE customer_id=?1 AND product_id = ?2 and is_Reviewed = 0 ORDER BY RAND() LIMIT 1",nativeQuery = true)
    Long getOrderDetailNotReviewed(Long customer_id,Long product_id);
    OrderDetail getById(Long id);
}
