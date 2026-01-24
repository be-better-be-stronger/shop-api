package com.shop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shop.security.handler.RestAccessDeniedHandler;
import com.shop.security.handler.RestAuthenticationEntryPoint;
import com.shop.security.jwt.JwtAuthFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter,
			RestAuthenticationEntryPoint authEntryPoint, RestAccessDeniedHandler accessDeniedHandler) throws Exception {

		http.csrf(csrf -> csrf.disable());

		http.cors(cors -> {
		}); // dùng CorsConfigurationSource đã define ở nơi khác

		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint) // ép 401 khi chưa login
				.accessDeniedHandler(accessDeniedHandler) // 403 khi đã login nhưng thiếu quyền
		);

		http.authorizeHttpRequests(auth -> auth
				// PUBLIC
				.requestMatchers("/uploads/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/ping").permitAll()
				.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()

				// USER/ADMIN
				.requestMatchers(HttpMethod.GET, "/api/auth/me").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/api/cart/**", "/api/orders/**").hasAnyRole("USER", "ADMIN")

				// ADMIN
				.requestMatchers("/api/admin/**").hasRole("ADMIN")

				// DEFAULT
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
