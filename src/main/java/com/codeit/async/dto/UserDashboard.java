package com.codeit.async.dto;

public record UserDashboard(
        String userName,
        String userEmail,
        String companyName,
        String mainTodo,
        String todoStatus
) {
}
