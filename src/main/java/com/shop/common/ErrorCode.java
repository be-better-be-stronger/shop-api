// src/main/java/com/shop/common/ErrorCode.java
package com.shop.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	ERR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED), 
	ERR_FORBIDDEN(HttpStatus.FORBIDDEN),

	ERR_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST),
	ERR_INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST),
	ERR_USER_INACTIVE(HttpStatus.FORBIDDEN),

	ERR_NOT_FOUND(HttpStatus.NOT_FOUND), 
	ERR_OUT_OF_STOCK(HttpStatus.BAD_REQUEST),
	ERR_CART_EMPTY(HttpStatus.BAD_REQUEST),

	ERR_BAD_REQUEST(HttpStatus.BAD_REQUEST),
	
	ERR_SERVER(HttpStatus.INTERNAL_SERVER_ERROR);

	private final HttpStatus status;

	ErrorCode(HttpStatus status) {
		this.status = status;
	}

}

