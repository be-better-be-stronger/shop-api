package com.shop.user.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.common.response.ApiResponse;
import com.shop.user.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;

	@PatchMapping("/{id}/disable")
	public ApiResponse<Void> disable(@PathVariable Integer id) {
		userService.disableUser(id);
	    return ApiResponse.ok(null);
	}
}
