package com.simonatb.bookstore.dto.author;

public record AuthorResponseDto(Long id, String name, String biography) {
    public long getId() {
        return 0;
    }
}
