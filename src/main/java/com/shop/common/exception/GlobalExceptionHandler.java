package com.shop.common.exception;

import com.shop.common.response.ApiResponse;

import jakarta.persistence.OptimisticLockException;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handle(ApiException e) {
		return ResponseEntity.status(e.getStatus()).body(new ApiResponse<>(false, e.getMessage(), null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException e) {
		String msg = e.getBindingResult().getFieldErrors().stream().findFirst()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).orElse("Validation error");
		return ResponseEntity.badRequest().body(new ApiResponse<>(false, msg, null));
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ApiResponse<Void>> handleOptimistic() {
		return ResponseEntity.status(409)
				.body(new ApiResponse<>(false, "Product was updated by another order, please retry", null));
	}

}
