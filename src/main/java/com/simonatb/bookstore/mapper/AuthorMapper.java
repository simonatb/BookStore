package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.author.AuthorCreateDto;
import com.simonatb.bookstore.dto.author.AuthorResponseDto;
import com.simonatb.bookstore.entity.Author;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponseDto toResponseDTO(Author author);

    Author toEntity(AuthorCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AuthorCreateDto dto, @MappingTarget Author author);

}
