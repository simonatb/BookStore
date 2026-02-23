package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.BookCreateDTO;
import com.simonatb.bookstore.dto.BookResponseDTO;
import com.simonatb.bookstore.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "author.name", target = "authorName")
    BookResponseDTO toResponseDTO(Book book);

    @Mapping(target = "author", ignore = true)
    Book toEntity(BookCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", ignore = true)
    void updateEntityFromDTO(BookCreateDTO dto, @MappingTarget Book book);
}
