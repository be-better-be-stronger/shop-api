package com.shop.config;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shop.security.jwt.JwtAuthFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// ✅ ép 401 khi chưa login
		http.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.accessDeniedHandler(new AccessDeniedHandlerImpl()) // 403 khi đã login nhưng thiếu quyền
		);

		http.authorizeHttpRequests(auth -> auth.requestMatchers("/error").permitAll().requestMatchers("/api/auth/**")
				.permitAll().requestMatchers("/api/categories/**").permitAll().requestMatchers("/api/products/**")
				.permitAll().requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
				.anyRequest().authenticated());

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	
		http.formLogin(f -> f.disable());
		http.logout(l -> l.disable());
		http.httpBasic(b -> b.disable());
		http.anonymous(a -> a.disable()); 

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
