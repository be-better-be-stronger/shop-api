package com.shop.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.auth.dto.request.LoginRequest;
import com.shop.auth.dto.request.RegisterRequest;
import com.shop.auth.dto.response.AuthResponse;
import com.shop.auth.service.AuthService;
import com.shop.cart.entity.Cart;
import com.shop.cart.repository.CartRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.security.jwt.JwtService;
import com.shop.user.entity.User;
import com.shop.user.entity.UserProfile;
import com.shop.user.entity.UserStatus;
import com.shop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
			throw new ApiException(ErrorCode.ERR_EMAIL_ALREADY_EXISTS);
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
	      .orElseThrow(() -> new ApiException(ErrorCode.ERR_INVALID_CREDENTIALS));

	  if (!encoder.matches(req.getPassword(), u.getPassword()))
	    throw new ApiException(ErrorCode.ERR_INVALID_CREDENTIALS);

	  if (!UserStatus.ACTIVE.equals(u.getStatus()))
	    throw new ApiException(ErrorCode.ERR_USER_INACTIVE);

	  String token = jwtService.generate(u.getEmail(), u.getRole().name());
	  return new AuthResponse(token);
	}

}
