package com.shop.order.service;

import com.shop.order.dto.CheckoutResponse;

public interface CheckoutTxService {
	CheckoutResponse checkoutOnce(String email);
}
