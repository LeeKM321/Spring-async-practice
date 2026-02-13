package com.codeit.async.dto;

public record PostResponseDto(
        int id,
        String title,
        String body,
        int userId
) {
}
