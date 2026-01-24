package com.shop.auth.service;

import com.shop.auth.dto.request.LoginRequest;
import com.shop.auth.dto.request.RegisterRequest;
import com.shop.auth.dto.response.AuthResponse;

public interface AuthService {
	void register(RegisterRequest req);

	AuthResponse login(LoginRequest req);
}
