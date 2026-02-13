package com.codeit.async.dto;

import java.util.List;

public record UserDashboard(
        String userName,
        String userEmail,
        String companyName,
        List<TodoResponse> todoList,
        int totalTodos,
        int completedTodos,
        String achievement
) {
}
