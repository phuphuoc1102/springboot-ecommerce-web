package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double totalPrice;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="shopping_cart_id",referencedColumnName = "shopping_cart_id")
    private ShoppingCart cart;
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="product_id",referencedColumnName = "product_id")
    private Product product;

}
