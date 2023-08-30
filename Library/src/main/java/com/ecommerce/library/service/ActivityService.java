package com.ecommerce.library.service;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {
    Activity save(Activity activity);
    List<Activity> findAll();
    Page<Activity> pageActivities(int pageNo, List<Activity> activityList);
}
