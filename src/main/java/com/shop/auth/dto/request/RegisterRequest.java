package com.shop.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class RegisterRequest {
	@Email
	@NotBlank
	private String email;

	@NotBlank
	@Size(min = 6)
	private String password;

	@NotBlank
	private String fullName;

	private String phone;
	private String address;
}
