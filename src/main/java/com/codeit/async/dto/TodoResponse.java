package com.codeit.async.dto;

public record TodoResponse(
        int id,
        int userId,
        String title,
        boolean completed
) {

}
