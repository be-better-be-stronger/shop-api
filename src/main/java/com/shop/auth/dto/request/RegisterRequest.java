package com.shop.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@Email(message = "{auth.email.invalid}")
	@NotBlank(message = "{auth.email.required}")
	private String email;

	@NotBlank(message = "{auth.password.required}")
	@Size(min = 6, max = 50, message = "auth.password.size")
	private String password;

	@NotBlank(message = "{auth.fullName.required}")
	@Size(max = 50, message = "{auth.fullName.size}")
	private String fullName;

	@NotBlank
	private String phone;
	
	@NotBlank
	private String address;
}
