package com.jobtracker.controller;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobApplicationController(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    @GetMapping
    public List<JobApplication> getAllApplications() {
        User currentUser = getCurrentUser();
        return jobApplicationRepository.findByUser(currentUser);
    }

    @GetMapping("/{id}")
    public JobApplication getApplicationById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application with id " + id + " not found"));

        if (!app.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You do not own this application");
        }

        return app;
    }

    @PostMapping
    public JobApplication createApplication(@Valid @RequestBody JobApplication application) {
        User currentUser = getCurrentUser();

        if (application.getDateApplied() == null) {
            application.setDateApplied(LocalDate.now());
        }

        application.setUser(currentUser);
        return jobApplicationRepository.save(application);
    }

    @PutMapping("/{id}")
    public JobApplication updateApplication(@PathVariable Long id, @Valid @RequestBody JobApplication updatedApplication) {
        User currentUser = getCurrentUser();

        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));

        if (!app.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You do not own this application");
        }

        if (updatedApplication.getCompanyName() != null) app.setCompanyName(updatedApplication.getCompanyName());
        if (updatedApplication.getJobTitle() != null) app.setJobTitle(updatedApplication.getJobTitle());
        if (updatedApplication.getStatus() != null) app.setStatus(updatedApplication.getStatus());
        if (updatedApplication.getDateApplied() != null) app.setDateApplied(updatedApplication.getDateApplied());
        if (updatedApplication.getNotes() != null) app.setNotes(updatedApplication.getNotes());

        return jobApplicationRepository.save(app);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application with id " + id + " not found"));

        if (!app.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You do not own this application");
        }

        jobApplicationRepository.delete(app);
        return ResponseEntity.noContent().build();
    }
}
