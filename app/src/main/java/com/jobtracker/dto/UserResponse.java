package com.jobtracker.dto;

import com.jobtracker.entity.User;

public record UserResponse(Long id, String email, String userName) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUserName());
    }
}
