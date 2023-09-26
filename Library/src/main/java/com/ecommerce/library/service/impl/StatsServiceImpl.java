package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Stats;
import com.ecommerce.library.repository.StatsRepository;
import com.ecommerce.library.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private StatsRepository statsRepository;
    @Override
    public List<Stats> StatsForMonthAndYear() {
        return statsRepository.StatsForMonthAndYear();
    }

    @Override
    public void updateStats() {
         statsRepository.updateStats();
    }
}
