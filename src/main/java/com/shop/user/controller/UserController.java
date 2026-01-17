package com.shop.user.controller;

import com.shop.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

  @GetMapping
  public ApiResponse<String> me(Authentication auth) {
    return ApiResponse.ok(auth.getName()); // email
  }
}
