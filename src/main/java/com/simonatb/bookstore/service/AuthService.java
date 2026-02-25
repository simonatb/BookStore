package com.simonatb.bookstore.service;

import com.simonatb.bookstore.dto.AuthResponseDTO;
import com.simonatb.bookstore.dto.LoginRequestDTO;
import com.simonatb.bookstore.dto.RegisterRequestDTO;
import com.simonatb.bookstore.entity.User;
import com.simonatb.bookstore.entity.VerificationToken;
import com.simonatb.bookstore.repository.UserRepository;
import com.simonatb.bookstore.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public String register(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User savedUser = userRepository.save(buildUser(dto));  // save first
        VerificationToken verificationToken = buildToken(savedUser);  // then build token with saved user

        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());

        return "Registration successful, please check your email to verify your account";
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        User user = userRepository.findByEmail(dto.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email before logging in");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }

    public String verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return "Email verified successfully, you can now log in";
    }

    private User buildUser(RegisterRequestDTO dto) {
        return User.builder()
            .name(dto.name())
            .email(dto.email())
            .password(passwordEncoder.encode(dto.password()))
            .role(User.Role.ROLE_USER)
            .enabled(false)
            .build();
    }

    private VerificationToken buildToken(User user) {
        String token = UUID.randomUUID().toString();
        return VerificationToken.builder()
            .token(token)
            .user(user)
            .expiresAt(LocalDateTime.now().plusHours(1))
            .build();
    }

}
