package com.ecommerce.library.service;

import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.Review;

import java.util.List;

public interface ReviewService {
    Review save(Review review);
    double getAvgRating(Product product);
    int getReviewCount(Product product);
    List<Review> getReviewByProduct(Product product);
    int countByProduct(Product product);
}
