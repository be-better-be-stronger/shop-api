package com.shop.common.response;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String code;
    private final T data;
    private final Map<String, String> errors;
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .code("OK")
                .data(data)
                .build();
    }
    
    public static ApiResponse<Void> ok() {
        return ApiResponse.<Void>builder()
                .code("OK")
                .build();
    }

    public static ApiResponse<Void> fail(String code, Map<String, String> errors) {
        return ApiResponse.<Void>builder()
                .code(code)
                .errors(errors)
                .build();
    }

}
