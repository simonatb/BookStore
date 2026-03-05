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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookResponseDto create(BookCreateDto dto) {
        Author author = authorRepository.findById(dto.authorId())
            .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + dto.authorId()));

        Book book = bookMapper.toEntity(dto);
        book.setAuthor(author);

        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    public BookResponseDto getById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
        return bookMapper.toResponseDTO(book);
    }

    public List<BookResponseDto> getAll() {
        return bookRepository.findAll()
            .stream()
            .map(bookMapper::toResponseDTO)
            .toList();
    }

    public BookResponseDto update(Long id, BookCreateDto dto) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("book with this id" + id + " not found"));

        if (dto.authorId() != null) {
            Author author = authorRepository.findById(dto.authorId())
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + dto.authorId()));
            book.setAuthor(author);
        }

        bookMapper.updateEntityFromDTO(dto, book);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("book with this id" + id + " not found"));

        bookRepository.delete(book);
    }

}
