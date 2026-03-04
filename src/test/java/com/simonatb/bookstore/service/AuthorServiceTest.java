package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.author.AuthorCreateDto;
import com.simonatb.bookstore.dto.author.AuthorResponseDto;
import com.simonatb.bookstore.entity.Author;
import com.simonatb.bookstore.exceptions.AuthorNotFoundException;
import com.simonatb.bookstore.mapper.AuthorMapper;
import com.simonatb.bookstore.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository; // Fake Database

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void testCreateSuccessfully() {
        AuthorCreateDto createDto = new AuthorCreateDto("J.K. Rowling", "biography");
        Author authorEntity = new Author();
        authorEntity.setName("J.K. Rowling");

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setName("J.K. Rowling");

        AuthorResponseDto expectedResponse = new AuthorResponseDto(1L, "J.K. Rowling", "biography");

        when(authorMapper.toEntity(createDto)).thenReturn(authorEntity);
        when(authorRepository.save(authorEntity)).thenReturn(savedAuthor);
        when(authorMapper.toResponseDTO(savedAuthor)).thenReturn(expectedResponse);

        AuthorResponseDto result = authorService.create(createDto);

        assertNotNull(result, "result shouldn't be null");
        assertEquals(1L, result.id(), "result id should be 1");
        assertEquals("J.K. Rowling", result.name(), "result name should be J.K. Rowling");

        verify(authorRepository, times(1)).save(authorEntity);
    }

    @Test
    void testGetByNameUnsuccessfully() {
        when(authorRepository.findByName("J.K. Rowling")).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.getByName("J.K. Rowling");
        }, "Should throw author not found exception");
    }

    @Test
    void testGetByNameSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setBiography("biography");
        author.setId(1L);

        AuthorResponseDto expectedResponse = new AuthorResponseDto(1L, "J.K. Rowling", "biography");

        when(authorRepository.findByName("J.K. Rowling")).thenReturn(Optional.of(author));
        when(authorMapper.toResponseDTO(author)).thenReturn(expectedResponse);

        AuthorResponseDto result = authorService.getByName("J.K. Rowling");

        assertNotNull(result, "result shouldn't be null");
        assertEquals("biography", result.biography(), "result biography should be biography");
        assertEquals("J.K. Rowling", result.name(), "result name should be J.K. Rowling");
        assertEquals(1L, result.id(), "result id should be 1");
    }

    @Test
    void testGetByIdInvalidAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.getById(1L);
        }, "Should throw author not found exception");
    }

    @Test
    void testGetByIdSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setBiography("biography");
        author.setId(1L);

        AuthorResponseDto expectedResponse = new AuthorResponseDto(1L, "J.K. Rowling", "biography");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toResponseDTO(author)).thenReturn(expectedResponse);

        AuthorResponseDto result = authorService.getById(1L);

        assertNotNull(result, "result shouldn't be null");
        assertEquals("biography", result.biography(), "result biography should be biography");
        assertEquals("J.K. Rowling", result.name(), "result name should be J.K. Rowling");
        assertEquals(1L, result.id(), "result id should be 1");
    }

    @Test
    void testDeleteInvalidAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.getById(1L);
        }, "Should throw author not found exception");
    }

    @Test
    void testUpdateSuccessfully() {

    }

}
