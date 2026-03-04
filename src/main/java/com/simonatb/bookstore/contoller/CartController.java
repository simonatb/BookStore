package com.simonatb.bookstore.contoller;

import com.simonatb.bookstore.dto.cart.AddToCartRequest;
import com.simonatb.bookstore.dto.cart.CartResponseDto;
import com.simonatb.bookstore.entity.User;
import com.simonatb.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addItemToCart(@AuthenticationPrincipal User user,
                                                        @RequestBody AddToCartRequest dto) {
        return ResponseEntity.ok(cartService.addItemToCart(user.getId(), dto));
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<CartResponseDto> removeFromCart(@AuthenticationPrincipal User user,
                                                          @PathVariable Long bookId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(user.getId(), bookId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }

}
