package com.shop.auth.service.impl;

import com.shop.auth.dto.request.*;
import com.shop.auth.dto.response.AuthResponse;
import com.shop.auth.service.AuthService;
import com.shop.cart.entity.Cart;
import com.shop.cart.repository.CartRepository;
import com.shop.common.exception.ApiException;
import com.shop.security.jwt.JwtService;
import com.shop.user.entity.User;
import com.shop.user.entity.UserProfile;
import com.shop.user.entity.UserStatus;
import com.shop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepo;
	private final CartRepository cartRepo;
	private final PasswordEncoder encoder;
	private final JwtService jwtService;

	@Override
	@Transactional
	public void register(RegisterRequest req) {
		if (userRepo.existsByEmail(req.getEmail())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
		}

		User u = new User();
		u.setEmail(req.getEmail());
		u.setPassword(encoder.encode(req.getPassword()));

		UserProfile p = new UserProfile();
		p.setUser(u);
		p.setFullName(req.getFullName());
		p.setPhone(req.getPhone());
		p.setAddress(req.getAddress());
		u.setProfile(p);

		userRepo.save(u);

		Cart c = new Cart();
		c.setUser(u);
		cartRepo.save(c);
	}

	@Override
	public AuthResponse login(LoginRequest req) {
	  var u = userRepo.findByEmail(req.getEmail())
	      .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

	  if (!encoder.matches(req.getPassword(), u.getPassword()))
	    throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

	  if (!UserStatus.ACTIVE.equals(u.getStatus()))
	    throw new ApiException(HttpStatus.FORBIDDEN, "User is not active");

	  String token = jwtService.generate(u.getEmail());
	  return new AuthResponse(token);
	}

}
