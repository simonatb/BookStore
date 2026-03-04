package com.simonatb.bookstore.repository;

import com.simonatb.bookstore.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
