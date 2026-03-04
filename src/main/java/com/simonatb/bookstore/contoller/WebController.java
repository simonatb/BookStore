package com.simonatb.bookstore.contoller;

import com.simonatb.bookstore.dto.cart.AddToCartRequest;
import com.simonatb.bookstore.dto.book.BookResponseDto;
import com.simonatb.bookstore.dto.cart.CartItemResponseDto;
import com.simonatb.bookstore.dto.cart.CartResponseDto;
import com.simonatb.bookstore.entity.User;
import com.simonatb.bookstore.entity.VerificationToken;
import com.simonatb.bookstore.exceptions.TokenNotFoundException;
import com.simonatb.bookstore.repository.TokenRepository;
import com.simonatb.bookstore.repository.UserRepository;
import com.simonatb.bookstore.service.AuthorService;
import com.simonatb.bookstore.service.BookService;
import com.simonatb.bookstore.service.CartService;
import com.simonatb.bookstore.service.EmailService;
import com.simonatb.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final int BOOKS_MAIN = 4;
    private static final int EXPIRATION_HOURS = 24;

    private final BookService bookService;
    private final OrderService orderService;
    private final CartService cartService;
    private final AuthorService authorService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        List<BookResponseDto> books = bookService.getAll().stream()
            .limit(BOOKS_MAIN)
            .toList();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/books/{id}")
    public String getBookPage(Model model, @PathVariable Long id) {
        model.addAttribute("book", bookService.getById(id));
        model.addAttribute("author", authorService.getByName(bookService.getById(id).authorName()));
        return "book";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId,
                            @AuthenticationPrincipal User user) {
        cartService.addItemToCart(user.getId(), new AddToCartRequest(bookId, 1L));
        return "redirect:/books";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long bookId,
                                 @AuthenticationPrincipal User user) {
        cartService.removeItemFromCart(user.getId(), bookId);
        return "redirect:/books";
    }

    @PostMapping("/cart/clear")
    public String clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return "redirect:/cart";
    }

    @PostMapping("/order")
    public String orderCart(@AuthenticationPrincipal User user) {
        orderService.createOrder(user.getId());
        return "redirect:/profile";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getOrdersByUserId(user.getId()));
        return "profile";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already in use");
            return "register";
        }
        User user = User.builder()
            .name(name)
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(User.Role.ROLE_USER)
            .enabled(false)
            .build();

        String tokenValue = UUID.randomUUID().toString();
        userRepository.save(user);

        VerificationToken token = VerificationToken.builder()
            .token(tokenValue)
            .user(user)
            .expiresAt(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
            .build();
        tokenRepository.save(token);

        emailService.sendVerificationEmail(email, tokenValue);

        return "redirect:/login?registered=true";
    }

    @Transactional
    @GetMapping("/api/auth/verify")
    public String confirm(@RequestParam String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenNotFoundException(String.format("Token: %s not found", token)));

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
        tokenRepository.delete(verificationToken);
        return "redirect:/login?verified=true";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/cart")
    public String cartPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("cart", cartService.getCart(user.getId()));
        return "cart";
    }

    @GetMapping("/books")
    public String booksPage(Model model,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String genre,
                            @AuthenticationPrincipal User user) {
        List<BookResponseDto> books = bookService.getAll().stream()
            .filter(b -> search == null || search.isEmpty() ||
                b.title().toLowerCase().contains(search.toLowerCase()) ||
                b.authorName().toLowerCase().contains(search.toLowerCase()) ||
                (b.description() != null && b.description().toLowerCase().contains(search.toLowerCase())))
            .filter(b -> genre == null || genre.isEmpty() || b.genre().toString().equalsIgnoreCase(genre))
            .toList();

        model.addAttribute("books", books);
        model.addAttribute("search", search);
        model.addAttribute("genre", genre);

        if (user != null) {
            CartResponseDto cart = cartService.getCart(user.getId());
            List<CartItemResponseDto> items = cart.items() != null ? cart.items() : List.of();

            BigDecimal cartPrice = items.stream()
                .map(CartItemResponseDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            Long cartQuantity = (long) items.size();
            model.addAttribute("cartPrice", cartPrice);
            model.addAttribute("cartQuantity", cartQuantity);
        }

        return "books";
    }

}