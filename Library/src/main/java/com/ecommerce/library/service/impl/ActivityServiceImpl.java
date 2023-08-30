package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Activity;
import com.ecommerce.library.repository.ActivityRepository;
import com.ecommerce.library.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    @Override
    public Activity save(Activity activity) {
        activity.setActivityTime(new Date());
        return activityRepository.save(activity);
    }

    @Override
    public Page<Activity> pageActivities(int pageNo, List<Activity> activityList) {
        Pageable pageable = PageRequest.of(pageNo, 12);
        //List<ProductDto> products = transfer(productDtoList);
        Page<Activity> productPage = toPage(activityList, pageable);
        return productPage;
    }

    private Page toPage(List<Activity> productList, Pageable pageable) {
        if (pageable.getOffset() >= productList.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = (pageable.getOffset() + pageable.getPageSize() > productList.size())
                ? productList.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = productList.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, productList.size());
    }

}
