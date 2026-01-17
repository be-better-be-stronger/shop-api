package com.shop.auth.controller;

import com.shop.auth.dto.request.*;
import com.shop.auth.dto.response.AuthResponse;
import com.shop.auth.service.AuthService;
import com.shop.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
  
}
