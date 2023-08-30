package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.CategoryRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setTotalPrice(0.0);
            cart.setTotalItems(0);
        }
        Set<CartItem> cartItems = cart.getCartItemSet();
        CartItem cartItem = findCartItem(cartItems, product.getId());
        if (cartItems == null) {
            cartItems = new HashSet<>();
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setTotalPrice(quantity * product.getSalePrice());
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);
            } else { // để khi click vào cart item ko null thì chỉ thay đổi số lượng và giá tiền
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setTotalPrice(cart.getTotalPrice() + (quantity * product.getSalePrice()));
                cartItemRepository.save(cartItem);
            }

        } else {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setTotalPrice(quantity * product.getSalePrice());
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                cartItemRepository.save(cartItem);

            } else {
                int itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
                cartItem.setTotalPrice(cart.getTotalPrice() + (quantity * product.getSalePrice()));
                cartItemRepository.save(cartItem);
            }
        }
        cart.setCartItemSet(cartItems);
        int totalItems = totalItems(cart.getCartItemSet());
        double totalPrice = totalPrice(cart.getCartItemSet());
        System.out.println("totalprice = " + totalPrice);
        cart.setTotalPrice(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setCustomer(customer);
        return shoppingCartRepository.save(cart);
    }

    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }


    private int totalItems(Set<CartItem> cartItems) {
        int totalItems = 0;
        for (CartItem cartItem : cartItems) {
            totalItems += cartItem.getQuantity();
        }
        return totalItems;
    }

    private double totalPrice(Set<CartItem> cartItems) {
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getTotalPrice();
        }
        return totalPrice;
    }

    @Override
    public ShoppingCart updateItemInCart(Product product, int quantity, Customer customer) {
        ShoppingCart shoppingCart = customer.getShoppingCart();
        Set<CartItem> cartItems = shoppingCart.getCartItemSet();
        CartItem cartItem = findCartItem(cartItems, product.getId());
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * product.getSalePrice());
        cartItemRepository.save(cartItem);
        int totalItems = totalItems(cartItems);
        double totalPrice = totalPrice(cartItems);
        shoppingCart.setTotalItems(totalItems);
        shoppingCart.setTotalPrice(totalPrice);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart deleteItemInCart(Product product, Customer customer) {
        ShoppingCart shoppingCart = customer.getShoppingCart();

        Set<CartItem>cartItems = shoppingCart.getCartItemSet();

        CartItem cartItem = findCartItem(cartItems, product.getId());

        cartItems.remove(cartItem);
        cartItemRepository.delete(cartItem);
        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);
        shoppingCart.setCartItemSet(cartItems);
        shoppingCart.setTotalPrice( totalPrice);
        shoppingCart.setTotalItems(totalItems);
        return shoppingCartRepository.save(shoppingCart);
    }
}
