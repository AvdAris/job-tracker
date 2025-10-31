package com.jobtracker.service;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    public List<JobApplication> getAllForCurrentUser() {
        return jobApplicationRepository.findByUser(getCurrentUser());
    }

    public JobApplication getById(Long id) {
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application with id " + id + " not found"));

        if (!app.getUser().getId().equals(getCurrentUser().getId())) {
            throw new SecurityException("Access denied: You do not own this application");
        }

        return app;
    }

    public JobApplication create(JobApplication application) {
        if (application.getDateApplied() == null) {
            application.setDateApplied(LocalDate.now());
        }
        application.setUser(getCurrentUser());
        return jobApplicationRepository.save(application);
    }

    public JobApplication update(Long id, JobApplication updatedApplication) {
        JobApplication app = getById(id);
        app.setCompanyName(updatedApplication.getCompanyName() != null ? updatedApplication.getCompanyName() : app.getCompanyName());
        app.setJobTitle(updatedApplication.getJobTitle() != null ? updatedApplication.getJobTitle() : app.getJobTitle());
        app.setStatus(updatedApplication.getStatus() != null ? updatedApplication.getStatus() : app.getStatus());
        app.setDateApplied(updatedApplication.getDateApplied() != null ? updatedApplication.getDateApplied() : app.getDateApplied());
        app.setNotes(updatedApplication.getNotes() != null ? updatedApplication.getNotes() : app.getNotes());

        return jobApplicationRepository.save(app);
    }

    public void delete(Long id) {
        JobApplication app = getById(id);
        jobApplicationRepository.delete(app);
    }
}
