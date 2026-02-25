package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.AddToCartRequest;
import com.simonatb.bookstore.dto.CartResponseDto;
import com.simonatb.bookstore.entity.Book;
import com.simonatb.bookstore.entity.Cart;
import com.simonatb.bookstore.entity.CartItem;
import com.simonatb.bookstore.entity.User;
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
            .orElseThrow(() -> new RuntimeException("Book not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getBook().getId().equals(dto.bookId()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.quantity());
            item.setSubTotal();
        } else {
            CartItem newItem = CartItem.builder()
                .book(book)
                .quantity(dto.quantity())
                .cart(cart)
                .build();
            newItem.setSubTotal();
            cart.addItem(newItem);
        }
        return cartMapper.toResponseDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartResponseDto removeItemFromCart(Long userId, Long bookId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart is empty"));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getBook().getId().equals(bookId))
            .findFirst();

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() - 1);
            item.setPrice(book.getPrice());
            item.setSubTotal();
            if (item.getQuantity() == 0) {
                cart.removeItem(item);
            }
        } else {
            throw new RuntimeException("Book not found in cart");
        }
        return cartMapper.toResponseDTO(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart is empty"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createInitialCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
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
