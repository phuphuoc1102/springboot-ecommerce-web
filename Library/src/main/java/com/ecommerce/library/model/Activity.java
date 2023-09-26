package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="activities")
public class Activity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="activity_id")
    private Long id;
    private String activity;
    private Date activityTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="admin_id",referencedColumnName = "admin_id")
    private Admin admin;
}
