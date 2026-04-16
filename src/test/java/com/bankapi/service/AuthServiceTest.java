package com.bankapi.service;

import com.bankapi.dto.request.RegisterRequest;
import com.bankapi.dto.response.AuthResponse;
import com.bankapi.exception.DuplicateResourceException;
import com.bankapi.model.User;
import com.bankapi.repository.UserRepository;
import com.bankapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("John Doe");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setDni("12345678A");
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_ShouldSucceed_WhenValidRequest() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-token");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void register_ShouldThrow_WhenEmailExists() {
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when DNI already exists")
    void register_ShouldThrow_WhenDniExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni("12345678A")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("DNI already registered");
    }
}
