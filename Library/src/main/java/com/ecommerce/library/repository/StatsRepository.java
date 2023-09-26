package com.ecommerce.library.repository;

import com.ecommerce.library.model.Stats;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface StatsRepository extends JpaRepository<Stats,Long> {
    @Query(value = "select * from stats",nativeQuery = true)
    public List<Stats> StatsForMonthAndYear();
    @Transactional
    @Modifying
    @Query(value = "REPLACE INTO stats(month_of_order, total_price_of_month, total_order_of_month)\n" +
            "SELECT DISTINCT(MONTH(order_date)) AS monthOfOrder, SUM(total_price) AS totalPriceOfMonth, COUNT(order_id) AS totalOrderOfMonth\n" +
            "FROM orders\n" +
            "WHERE YEAR(order_date) = 2023\n" +
            "GROUP BY monthOfOrder\n" +
            "ORDER BY monthOfOrder desc;",nativeQuery = true)
    public void updateStats();

}
