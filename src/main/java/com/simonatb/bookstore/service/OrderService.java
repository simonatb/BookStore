package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.order.OrderResponseDto;
import com.simonatb.bookstore.entity.cart.Cart;
import com.simonatb.bookstore.entity.order.Order;
import com.simonatb.bookstore.entity.order.OrderItem;
import com.simonatb.bookstore.mapper.OrderMapper;
import com.simonatb.bookstore.repository.CartRepository;
import com.simonatb.bookstore.repository.OrderRepository;
import com.simonatb.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponseDto createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart is empty"));

        Order order = new Order();
        order.setUser(userRepository.getReferenceById(userId));
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());

        List<OrderItem> orderItems = cart.getItems().stream()
            .map(orderMapper::mapToOrderItem)
            .peek(item -> item.setOrder(order))
            .toList();

        order.setItems(orderItems);

        orderRepository.save(order);
        cartService.clearCart(userId);

        return orderMapper.toResponseDTO(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

}
