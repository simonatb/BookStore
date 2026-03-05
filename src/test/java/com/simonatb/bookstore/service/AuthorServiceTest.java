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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

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
            authorService.delete(1L);
        }, "Should throw author not found exception");
    }

    @Test
    void testDeleteSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setBiography("biography");
        author.setId(1l);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        authorService.delete(1L);
        verify(authorRepository).delete(author);
    }

    @Test
    void testUpdateUnsuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        AuthorCreateDto dto = new AuthorCreateDto("name", "bio");

        assertThrows(AuthorNotFoundException.class, () -> {
            authorService.update(dto,1L);
        }, "Should throw author not found exception");
    }

    @Test void testUpdateSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setBiography("biography");
        author.setId(1l);

        AuthorCreateDto dto = new AuthorCreateDto("new name", "new bio");

        AuthorResponseDto expectedResponse = new AuthorResponseDto(1L, "new name", "new bio");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toResponseDTO(author)).thenReturn(expectedResponse);

        AuthorResponseDto result = authorService.update(dto, 1L);

        assertNotNull(result, "Should not be null");
        assertEquals("new name", result.name(), "The name should be new name");
        assertEquals("new bio", result.biography(), "The biography should be new bio");

        verify(authorRepository).findById(1L);
        verify(authorMapper).updateEntityFromDTO(dto, author);
        verify(authorRepository).save(author);
    }

    @Test
    void testGetAllSuccessfully() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("Stephen King");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("George R.R. Martin");

        List<Author> authors = List.of(author1, author2);

        AuthorResponseDto dto1 = new AuthorResponseDto(1L, "Stephen King", "Bio 1");
        AuthorResponseDto dto2 = new AuthorResponseDto(2L, "George R.R. Martin", "Bio 2");

        when(authorRepository.findAll()).thenReturn(authors);

        when(authorMapper.toResponseDTO(author1)).thenReturn(dto1);
        when(authorMapper.toResponseDTO(author2)).thenReturn(dto2);

        List<AuthorResponseDto> result = authorService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size(), "The list should contain exactly 2 authors");
        assertEquals("Stephen King", result.get(0).name(), "Fist name should be Stephen Kigs");
        assertEquals("George R.R. Martin", result.get(1).name(), "Second name should be George R.R. Martin");

        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEmptyList() {
        when(authorRepository.findAll()).thenReturn(List.of());

        List<AuthorResponseDto> result = authorService.getAll();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "The list should be empty");
        verify(authorRepository).findAll();
    }

}
