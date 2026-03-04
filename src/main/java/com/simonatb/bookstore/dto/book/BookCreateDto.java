package com.simonatb.bookstore.dto.book;

import com.simonatb.bookstore.entity.Book;

import java.math.BigDecimal;

public record BookCreateDto(String title, Book.Genre genre, Long authorId, String description, BigDecimal price) { }
