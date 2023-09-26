package com.ecommerce.library.repository;

import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    @Query(value = "select avg(rating) from review where product_id = ?1",nativeQuery = true)
    int getAvgRating(Long product_id);
    @Query(value = "select count(*) from review where product_id = ?1",nativeQuery = true)
    int getReviewCount(Long product_id);
    List<Review> getReviewByProduct(Product product);
    int countByProduct(Product product);

}
