// src/main/java/com/shop/common/ErrorCode.java
package com.shop.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	ERR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"), 
	ERR_FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),

	ERR_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email already exists"),
	ERR_INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid credentials"),
	ERR_USER_INACTIVE(HttpStatus.FORBIDDEN, "User is inactive"),

	ERR_NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"), ERR_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Out of stock"),
	ERR_CART_EMPTY(HttpStatus.BAD_REQUEST, "Cart is empty"),

	ERR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
	
	ERR_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "Upload fail");

	private final HttpStatus status;
	private final String defaultMessage;

	ErrorCode(HttpStatus status, String defaultMessage) {
		this.status = status;
		this.defaultMessage = defaultMessage;
	}

}

