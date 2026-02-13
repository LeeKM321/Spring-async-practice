package com.codeit.async.dto;


public record UserResponse(
        int id,
        String name,
        String email,
        String phone,
        Company company
) {

    public record Company(
            String name,
            String catchPhrase,
            String bs
    ) {

    }
}
