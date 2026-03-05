package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.book.BookCreateDto;
import com.simonatb.bookstore.dto.book.BookResponseDto;
import com.simonatb.bookstore.entity.Author;
import com.simonatb.bookstore.entity.Book;
import com.simonatb.bookstore.exceptions.AuthorNotFoundException;
import com.simonatb.bookstore.exceptions.BookNotFoundException;
import com.simonatb.bookstore.mapper.BookMapper;
import com.simonatb.bookstore.repository.AuthorRepository;
import com.simonatb.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void testCreateUnsuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        BookCreateDto dto = new BookCreateDto("Harry Potter", Book.Genre.FICTION,
            1L, "no description", BigDecimal.valueOf(20));

        assertThrows(AuthorNotFoundException.class, () -> {
            bookService.create(dto);
        }, "Should throw author not found exception");
    }

    @Test
    void testCreateSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        BookCreateDto createDto = new BookCreateDto("Harry Potter", Book.Genre.FICTION,
            1L, "no description", BigDecimal.valueOf(20));

        Book book = new Book(1L, "Harry Potter", author ,"no description",
            BigDecimal.valueOf(20), Book.Genre.FICTION, null);

        BookResponseDto responseDto = new BookResponseDto(1L, "Harry Potter", Book.Genre.FICTION,
            "J.K. Rowling", "no description", BigDecimal.valueOf(20), null);

        when(bookMapper.toEntity(createDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDTO(book)).thenReturn(responseDto);

        BookResponseDto result = bookService.create(createDto);

        assertNotNull(result, "Should not be null");
        assertEquals("Harry Potter", result.title(), "The title of the saved book should be Harry Potter");

        verify(authorRepository).findById(1L);
        verify(bookRepository).save(book);
        verify(bookMapper).toResponseDTO(book);
    }

    @Test
    void testGetByIdUnsuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
            () -> {bookService.getById(1L); }, "Should throw book not found because it doesnt exist");
    }

    @Test
    void testGetByIdSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setId(1L);

        Book book = new Book();
        book.setTitle("Harry Potter");
        book.setAuthor(author);
        book.setId(1L);

        BookResponseDto expectedDto = new BookResponseDto(1L, "Harry Potter", Book.Genre.FICTION,
            "J.K. Rowling", "no description", BigDecimal.valueOf(20), null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponseDTO(book)).thenReturn(expectedDto);

        BookResponseDto result = bookService.getById(1L);

        assertNotNull(result, "Service should return a DTO");
        assertEquals("Harry Potter", result.title(), "The title should match");
        assertEquals("J.K. Rowling", result.authorName(), "The author name should match");

        verify(bookRepository).findById(1L);
    }

    @Test
    void testGetAllEmptyList() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<BookResponseDto> result = bookService.getAll();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "The list should be empty");
        verify(bookRepository).findAll();
    }

    @Test
    void testGetAllSuccessfully() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("The Hobbit");

        Book book2 = new Book();
        book2.setId(2L);
        book1.setTitle("LOTR");

        List<Book> books = List.of(book1, book2);

        BookResponseDto dto1 = new BookResponseDto(1L, "The Hobbit", Book.Genre.FICTION,
            "Tolkin", "nope", BigDecimal.valueOf(20), null);
        BookResponseDto dto2 = new BookResponseDto(2L, "LOTR", Book.Genre.FICTION,
            "Tolkin", "nope", BigDecimal.valueOf(20), null);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toResponseDTO(book1)).thenReturn(dto1);
        when(bookMapper.toResponseDTO(book2)).thenReturn(dto2);

        List<BookResponseDto> result = bookService.getAll();

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "The list should contain exactly 2 books");
        assertEquals("The Hobbit", result.get(0).title(), "First title should be The Hobbit");
        assertEquals("Tolkin", result.get(1).authorName(), "Second authorName should be Tolkin");

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUnsuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {bookService.getById(1L); },
            "Should throw book not found because it doesnt exist");
    }

    @Test
    void testUpdateSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setId(1L);

        Book book = new Book(1L, "Harry Potter", author ,"no description",
            BigDecimal.valueOf(20), Book.Genre.FICTION, null);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookCreateDto createDto = new BookCreateDto("Harry Potter 2", Book.Genre.FICTION,
            1L, "no description", BigDecimal.valueOf(20));

        BookResponseDto responseDto = new BookResponseDto(1L, "Harry Potter 2", Book.Genre.FICTION,
            "J.K. Rowling", "no description", BigDecimal.valueOf(20), null);

        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDTO(book)).thenReturn(responseDto);

        BookResponseDto result = bookService.update(1L, createDto);

        assertNotNull(result, "Should not be null");
        assertEquals("Harry Potter 2", result.title(), "The title should be Harry Potter 2");

        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(bookMapper).updateEntityFromDTO(createDto, book);
        verify(bookRepository).save(book);
    }

    @Test
    void testDeleteBookUnsuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.delete(1L);
        }, "Should throw book not found exception");
    }

    @Test
    void testDeleteSuccessfully() {
        Author author = new Author();
        author.setName("J.K. Rowling");
        author.setId(1L);

        Book book = new Book(1L, "Harry Potter", author ,"no description",
            BigDecimal.valueOf(20), Book.Genre.FICTION, null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.delete(1L);
        verify(bookRepository).delete(book);
    }

}
