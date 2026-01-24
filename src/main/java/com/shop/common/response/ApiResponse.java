package com.shop.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;

	public static <T> ApiResponse<T> ok(T data) {
		return ApiResponse.<T>builder().success(true).message("OK").data(data).build();
	}

	public static <T> ApiResponse<T> fail(String message) {
		return ApiResponse.<T>builder().success(false).message(message).data(null).build();
	}

}
