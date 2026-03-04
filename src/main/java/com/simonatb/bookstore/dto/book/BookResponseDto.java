package com.simonatb.bookstore.dto.book;

import com.simonatb.bookstore.entity.Book;

import java.math.BigDecimal;

public record BookResponseDto(Long id, String title, Book.Genre genre, String authorName,
                              String description, BigDecimal price, String imageUrl) { }

