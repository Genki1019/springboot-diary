package com.genki.rest_api.diary.dto;

public record ApiErrorResponseDto(
        String message,
        ApiDetailErrorResponseDto apiDetailErrorResponseDto
) {
    public ApiErrorResponseDto(String message) {
        this(message, null);
    }
}
