// src/main/java/com/shop/common/ApiException.java
package com.shop.common.exception;

import org.springframework.http.HttpStatus;

import com.shop.common.ErrorCode;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final ErrorCode code;

	public ApiException(ErrorCode code) {
		super(code.getDefaultMessage());
		this.code = code;
	}

	public ApiException(ErrorCode code, String message) {
		super(message);
		this.code = code;
	}

	public HttpStatus getStatus() {
		return code.getStatus();
	}

	public String getCode() {
		return code.name();
	}
}
