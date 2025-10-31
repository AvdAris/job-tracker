package com.jobtracker.service;

import com.jobtracker.entity.User;
import com.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_createsUser_whenEmailIsNew() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = userService.register("new@example.com", "secret", "Alice");

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getUserName()).isEqualTo("Alice");
        assertThat(user.getPasswordHash()).isEqualTo("hashed123");

        verify(userRepository).save(any(User.class));
    }

   @Test
    void register_throwsConflict_whenEmailExists() {
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                userService.register("dup@example.com", "secret", "Bob")
        )
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Email already in use")
        .matches(ex -> ((ResponseStatusException) ex).getStatusCode().value() == CONFLICT.value());

        verify(userRepository, never()).save(any());
    }

}
