package com.simonatb.bookstore.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDto(Long id, Long bookId, String bookTitle, Long quantity, BigDecimal totalPrice) {
}

