package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.book.BookCreateDto;
import com.simonatb.bookstore.dto.book.BookResponseDto;
import com.simonatb.bookstore.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "author.name", target = "authorName")
    BookResponseDto toResponseDTO(Book book);

    @Mapping(target = "author", ignore = true)
    Book toEntity(BookCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    void updateEntityFromDTO(BookCreateDto dto, @MappingTarget Book book);

}