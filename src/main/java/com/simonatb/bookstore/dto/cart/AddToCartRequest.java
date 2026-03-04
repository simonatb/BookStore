package com.simonatb.bookstore.dto.cart;

public record AddToCartRequest(Long bookId, Long quantity) {
}
