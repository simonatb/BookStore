package com.simonatb.bookstore.dto;

public record AddToCartRequest(Long bookId, Long quantity) {
}
