package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="shopping_cart_id ")
    private Long id;
    private int totalItems = 0;
    private double totalPrice = 0.0;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id",referencedColumnName = "customer_id")
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart")
    private Set<CartItem> cartItemSet;
}
