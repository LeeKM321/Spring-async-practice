package com.codeit.async.dto;

public record PostRequestDto(
        String title,
        String body,
        int userId
) {
}
