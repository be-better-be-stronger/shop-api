package com.shop.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	@Override
	protected boolean shouldNotFilter(HttpServletRequest req) {
		String path = req.getRequestURI();

        // ✅ Chỉ skip đúng 2 endpoint public của auth
        if (path.equals("/api/auth/login")) return true;
        if (path.equals("/api/auth/register")) return true;

        // (tuỳ chọn) ping public
        if (path.equals("/api/ping")) return true;

        return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String h = req.getHeader("Authorization");
		if (h == null || !h.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}

		String token = h.substring(7);
		try {
			String email = jwtService.extractEmail(token);
			String role = jwtService.extractRole(token); // "ADMIN" / "USER"

			var auth = new UsernamePasswordAuthenticationToken(email, null, java.util.List
					.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role)));
			SecurityContextHolder.getContext().setAuthentication(auth);

			chain.doFilter(req, res);
		} catch (ExpiredJwtException e) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json");
			res.getWriter().write("""
					  {
					    "status": 401,
					    "message": "JWT expired. Please login again."
					  }
					""");
		}
	}
}
