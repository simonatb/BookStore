package com.simonatb.bookstore.repository;

import com.simonatb.bookstore.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
