package com.shop.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class LoginRequest {
	@Email
	@NotBlank
	@Size(max = 120)
	private String email;

	@NotBlank
	@Size(min = 6, max = 64)
	private String password;
}