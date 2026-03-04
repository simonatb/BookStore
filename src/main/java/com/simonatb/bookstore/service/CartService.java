package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.cart.AddToCartRequest;
import com.simonatb.bookstore.dto.cart.CartResponseDto;
import com.simonatb.bookstore.entity.Book;
import com.simonatb.bookstore.entity.cart.Cart;
import com.simonatb.bookstore.entity.cart.CartItem;
import com.simonatb.bookstore.entity.User;
import com.simonatb.bookstore.exceptions.BookNotFoundException;
import com.simonatb.bookstore.exceptions.EmptyCartException;
import com.simonatb.bookstore.exceptions.UserNotFoundException;
import com.simonatb.bookstore.mapper.CartMapper;
import com.simonatb.bookstore.repository.BookRepository;
import com.simonatb.bookstore.repository.CartRepository;
import com.simonatb.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartResponseDto addItemToCart(Long userId, AddToCartRequest dto) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> createInitialCart(userId));

        Book book = bookRepository.findById(dto.bookId())
            .orElseThrow(() -> new BookNotFoundException(String.format("Book with id: %d not found", dto.bookId())));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getBook().getId().equals(dto.bookId()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.quantity());
        } else {
            CartItem newItem = CartItem.builder()
                .book(book)
                .quantity(dto.quantity())
                .cart(cart)
                .price(book.getPrice())
                .build();
            cart.addItem(newItem);
        }
        cart.updateTotalAmount();
        return cartMapper.toResponseDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartResponseDto removeItemFromCart(Long userId, Long bookId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new EmptyCartException(String.format("Cart with user id: %d is empty", userId)));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getBook().getId().equals(bookId))
            .findFirst();

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(String.format("Book with id: %d not found", bookId)));

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() - 1);
            item.setPrice(book.getPrice());
            item.getSubTotal();
            if (item.getQuantity() == 0) {
                cart.removeItem(item);
            }
        }
        cart.updateTotalAmount();
        return cartMapper.toResponseDTO(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new EmptyCartException(String.format("Cart with user id: %d is empty", userId)));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createInitialCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %d not found", userId)));
        return Cart.builder().user(user).build();
    }

    public CartResponseDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElse(null);

        if (cart == null) {
            return new CartResponseDto(null, userId, List.of(), BigDecimal.ZERO);
        }
        return cartMapper.toResponseDTO(cart);
    }

}
