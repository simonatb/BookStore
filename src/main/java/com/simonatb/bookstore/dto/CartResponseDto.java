package com.simonatb.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto<CartItemResponseDto>(Long id, Long userId, List<CartItemResponseDto> items, BigDecimal totalAmount) {

}
