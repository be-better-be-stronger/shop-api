package com.shop.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.auth.dto.request.LoginRequest;
import com.shop.auth.dto.request.RegisterRequest;
import com.shop.auth.dto.response.AuthResponse;
import com.shop.auth.service.AuthService;
import com.shop.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest req) {
		authService.register(req);
		return ApiResponse.ok(null);
	}

	@PostMapping("/login")
	public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
		return ApiResponse.ok(authService.login(req));
	}

	@GetMapping("/me")
	public ApiResponse<Map<String, Object>> me() {
	    Map<String, Object> user = new HashMap<>();
	    user.put("email", "a@gmail.com");
	    user.put("role", "USER");
	    return ApiResponse.ok(user);
	}

}
