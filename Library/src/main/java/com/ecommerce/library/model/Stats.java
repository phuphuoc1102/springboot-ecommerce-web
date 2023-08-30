package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Stats {
    @Id
    @Column(name="month_of_order ")
    private int monthOfOrder;
    private long totalPriceOfMonth;
    private int totalOrderOfMonth;
}

