package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.AuthorCreateDto;
import com.simonatb.bookstore.dto.AuthorResponseDto;
import com.simonatb.bookstore.entity.Author;
import com.simonatb.bookstore.mapper.AuthorMapper;
import com.simonatb.bookstore.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorResponseDto create(AuthorCreateDto dto) {
        Author author = authorMapper.toEntity(dto);
        return authorMapper.toResponseDTO(authorRepository.save(author));
    }

    public AuthorResponseDto getById(Long id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No author found with id " + id));
        return authorMapper.toResponseDTO(author);
    }

    public List<AuthorResponseDto> getAll() {
        return authorRepository.findAll()
            .stream()
            .map(authorMapper::toResponseDTO)
            .toList();
    }

    public AuthorResponseDto update(AuthorCreateDto dto, Long id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No author found with id " + id));

        authorMapper.updateEntityFromDTO(dto, author);
        return authorMapper.toResponseDTO(authorRepository.save(author));
    }

    public void delete(Long id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No author found with id " + id));

        authorRepository.delete(author);
    }

}
