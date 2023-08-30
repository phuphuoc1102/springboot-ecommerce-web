package com.ecommerce.library.service;

import com.ecommerce.library.model.Stats;

import java.util.List;

public interface StatsService {
    List<Stats> StatsForMonthAndYear();
    public void updateStats();
}
