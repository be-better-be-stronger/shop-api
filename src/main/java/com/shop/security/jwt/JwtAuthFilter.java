package com.shop.security.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService uds;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    String p = req.getServletPath();
    return p.startsWith("/api/auth/");
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
    String email = jwtService.extractEmail(token);

    var ud = uds.loadUserByUsername(email);
    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
    SecurityContextHolder.getContext().setAuthentication(auth);

    chain.doFilter(req, res);
  }
}
