package com.shop.common.exception;

public enum ErrorCode {

	// ===== AUTH =====
	 ERR_EMAIL_ALREADY_EXISTS,
	  ERR_INVALID_CREDENTIALS,	  
	  
	// ===== USER =====
	  ERR_USER_INACTIVE,
	  ERR_USER_NOT_FOUND,
	  
	  // ===== CART =====
	  ERR_CART_EMPTY,
	  ERR_CART_NOT_FOUND,
	  ERR_CART_ITEM_NOT_FOUND,
	  ERR_NOT_YOUR_CART_ITEM,

	// ===== PRODUCT =====
	  ERR_CATEGORY_NOT_FOUND,
	  
	  // ===== PRODUCT =====
	  ERR_PRODUCT_NOT_FOUND,
	  ERR_PRODUCT_INACTIVE,
	  ERR_NOT_ENOUGH_STOCK,

	  // ===== ORDER =====
	  ERR_CHECKOUT_CONFLICT,

	  // ===== COMMON =====
	  ERR_VALIDATION,
	  ERR_SYSTEM
	}
