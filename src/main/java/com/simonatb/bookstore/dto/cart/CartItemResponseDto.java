package com.simonatb.bookstore.dto.cart;

import java.math.BigDecimal;

public record CartItemResponseDto(Long id, Long bookId, String bookTitle, Long quantity, BigDecimal totalPrice) {
}
