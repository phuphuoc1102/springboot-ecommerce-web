package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.Review;
import com.ecommerce.library.repository.ReviewRepository;
import com.ecommerce.library.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public double getAvgRating(Product product) {
        return reviewRepository.getAvgRating(product.getId());
    }

    @Override
    public int getReviewCount(Product product) {
        return reviewRepository.getReviewCount(product.getId());
    }

    @Override
    public List<Review> getReviewByProduct(Product product) {
        return reviewRepository.getReviewByProduct(product);
    }

    @Override
    public int countByProduct(Product product) {
        return reviewRepository.countByProduct(product);
    }
}
