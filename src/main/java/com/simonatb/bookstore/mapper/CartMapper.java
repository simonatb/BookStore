package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.cart.CartItemResponseDto;
import com.simonatb.bookstore.dto.cart.CartResponseDto;
import com.simonatb.bookstore.entity.cart.Cart;
import com.simonatb.bookstore.entity.cart.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDto toResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponseDto> itemDtos = cart.getItems().stream()
            .map(this::toCartItemDTO)
            .collect(Collectors.toList());

        return new CartResponseDto(cart.getId(), cart.getUser().getId(), itemDtos, cart.getTotalAmount());
    }

    public CartItemResponseDto toCartItemDTO(CartItem item) {
        if (item == null) {
            return null;
        }

        return new CartItemResponseDto(item.getId(), item.getBook().getId(),
            item.getBook().getTitle(), item.getQuantity(), item.getSubTotal());
    }

}

