package com.shop.user.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.user.entity.UserStatus;
import com.shop.user.repository.UserRepository;
import com.shop.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepo;

	@Override
	@Transactional
	public void disableUser(Integer id) {
		var u = userRepo.findById(id)
		        .orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND));

		    if (u.getStatus() != UserStatus.DISABLED) {
		      u.setStatus(UserStatus.DISABLED);
		      u.setDisabledAt(LocalDateTime.now());
		    }		
	}

}
