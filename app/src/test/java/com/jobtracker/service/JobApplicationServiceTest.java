package com.jobtracker.service;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock private JobApplicationRepository jobRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks private JobApplicationService service;

    private User currentUser;

    @BeforeEach
    void setupSecurityContext() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("user@example.com");

        // Set fake authentication for current user
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(currentUser.getEmail(), null)
        );
    }

    @Test
    void create_setsDateAndUser_whenSaving() {
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(jobRepo.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        JobApplication app = new JobApplication();
        app.setCompanyName("Google");
        app.setJobTitle("Engineer");

       
        JobApplication saved = service.create(app);

        assertThat(saved.getUser()).isEqualTo(currentUser);
        assertThat(saved.getDateApplied()).isNotNull();
        verify(jobRepo).save(any(JobApplication.class));
    }

    @Test
    void getById_throws_whenNotFound() {
        when(jobRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
