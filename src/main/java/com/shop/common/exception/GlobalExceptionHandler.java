package com.shop.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shop.common.response.ApiResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handle(ApiException e) {
		return buildResponseEntity(e.getStatus(), e.getMessage());
	}	

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException e) {
		String msg = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.findFirst()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.orElse(ErrorCode.ERR_VALIDATION.name());		
		return buildResponseEntity(HttpStatus.BAD_REQUEST, msg);
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ApiResponse<Void>> handleOptimistic() {
		return buildResponseEntity(HttpStatus.CONFLICT, ErrorCode.ERR_CHECKOUT_CONFLICT.name());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handle(Exception e) {
		log.error("Unhandled exception", e);
		return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_SYSTEM.name());
	}
	
	private ResponseEntity<ApiResponse<Void>> buildResponseEntity(HttpStatus status, String message){
		return ResponseEntity.status(status).body(ApiResponse.fail(message));
	}

}
