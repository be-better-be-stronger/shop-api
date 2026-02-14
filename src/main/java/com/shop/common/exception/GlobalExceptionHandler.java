// src/main/java/com/shop/common/GlobalExceptionHandler.java
package com.shop.common.exception;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.shop.common.ErrorCode;
import com.shop.common.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleBodyValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();

		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			if (isTypeMismatch(fe)) {
	            errors.putIfAbsent(fe.getField(), "Invalid value");
	        } else {
	            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
	        }
		}

		ErrorCode code = ErrorCode.ERR_VALIDATION;

		return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.name(), errors));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleParamValidation(ConstraintViolationException ex) {
		Map<String, String> errors = new LinkedHashMap<>();

		ex.getConstraintViolations().forEach(v -> {
			String field = v.getPropertyPath().toString(); // e.g. list.page
			errors.putIfAbsent(field, v.getMessage());
		});

		ErrorCode code = ErrorCode.ERR_VALIDATION;

		return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.name(), errors));
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
		return ResponseEntity.status(e.getStatus()).body(ApiResponse.fail(e.getCode(), null));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
		ErrorCode code = ErrorCode.ERR_NOT_FOUND;

		return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.name(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
		ErrorCode code = ErrorCode.ERR_SERVER;
		return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.name(), null));
	}

	private boolean isTypeMismatch(FieldError fe) {
		if (fe.isBindingFailure())
			return true;
		String code = fe.getCode();
		if (code != null && code.startsWith("typeMismatch"))
			return true;

		String[] codes = fe.getCodes();
		return codes != null && Arrays.stream(codes).anyMatch(c -> c != null && c.startsWith("typeMismatch"));
	}
}
