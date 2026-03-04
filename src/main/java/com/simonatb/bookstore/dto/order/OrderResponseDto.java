package com.simonatb.bookstore.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDto(Long id, Long userId, List<OrderItemResponseDto> items, BigDecimal totalAmount) {
}
