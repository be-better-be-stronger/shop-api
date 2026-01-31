package com.shop.security;

import java.util.List;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shop.user.entity.UserStatus;
import com.shop.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var u = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!UserStatus.ACTIVE.equals(u.getStatus())) {
      throw new DisabledException("User is not active");
    }

    return new org.springframework.security.core.userdetails.User(
        u.getEmail(),
        u.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()))
    );
  }
}
