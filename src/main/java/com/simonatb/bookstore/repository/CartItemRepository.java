package com.simonatb.bookstore.repository;

import com.simonatb.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(long cartId);

    Optional<CartItem> findByCartIdAndId(long cartId, long id);

}
