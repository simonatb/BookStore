package com.simonatb.bookstore.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(Long id, Long userId, List<CartItemResponseDto> items, BigDecimal totalAmount) {

}
