package com.shop.auth.service;

import com.shop.auth.dto.request.*;
import com.shop.auth.dto.response.AuthResponse;

public interface AuthService {
  void register(RegisterRequest req);
  AuthResponse login(LoginRequest req);
}
