package com.articlehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .code(0)
            .message("Success")
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .code(0)
            .message(message)
            .data(data)
            .build();
    }

    public static ApiResponse<?> error(Integer code, String message) {
        return ApiResponse.builder()
            .code(code)
            .message(message)
            .build();
    }

    public static ApiResponse<?> error(String message) {
        return ApiResponse.builder()
            .code(1)
            .message(message)
            .build();
    }
}
