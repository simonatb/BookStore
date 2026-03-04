package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.order.OrderItemResponseDto;
import com.simonatb.bookstore.dto.order.OrderResponseDto;
import com.simonatb.bookstore.entity.cart.CartItem;
import com.simonatb.bookstore.entity.order.Order;
import com.simonatb.bookstore.entity.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderItemResponseDto toOrderItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResponseDto(item.getId(), item.getBook().getId(),
            item.getBook().getTitle(), item.getQuantity(), item.getSubTotal());
    }

    public OrderResponseDto toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
            .map(this::toOrderItemDTO)
            .collect(Collectors.toList());

        return new OrderResponseDto(order.getId(), order.getUser().getId(), itemDtos, order.getTotalAmount());
    }

    public OrderItem mapToOrderItem(CartItem item) {
        return OrderItem.builder().book(item.getBook()).price(item.getPrice())
            .quantity(item.getQuantity()).subTotal(item.getSubTotal()).build();
    }

}
