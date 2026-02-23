package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.BookCreateDTO;
import com.simonatb.bookstore.dto.BookResponseDTO;
import com.simonatb.bookstore.entity.Author;
import com.simonatb.bookstore.entity.Book;
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

    public BookResponseDTO create(BookCreateDTO dto) {
        Author author = authorRepository.findById(dto.authorId())
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.authorId()));

        Book book = bookMapper.toEntity(dto);
        book.setAuthor(author);

        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    public BookResponseDTO getById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("book with this id" + id + " not found"));
        return bookMapper.toResponseDTO(book);
    }

    public List<BookResponseDTO> getAll() {
        return bookRepository.findAll()
            .stream()
            .map(bookMapper::toResponseDTO)
            .toList();
    }

    public BookResponseDTO update(Long id, BookCreateDTO dto) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("book with this id" + id + " not found"));

        if (dto.authorId() != null) {
            Author author = authorRepository.findById(dto.authorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.authorId()));
            book.setAuthor(author);
        }

        bookMapper.updateEntityFromDTO(dto, book);
        return bookMapper.toResponseDTO(bookRepository.save(book));
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("book with this id" + id + " not found"));

        bookRepository.delete(book);
    }

}
