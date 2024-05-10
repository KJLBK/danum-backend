package com.danum.danum.filter;

import com.danum.danum.util.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer";

	private static final Integer SUBSTRING_INDEX = 7;

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);
		if (token != null) {
			validateToken(token);
		}

		filterChain.doFilter(request, response);
	}

	private void validateToken(String token) {
		jwtUtil.validate(token);
	}

	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (StringUtils.hasText(token) || !token.startsWith(PREFIX)) {
			return null;
		}

		return token.substring(SUBSTRING_INDEX);
	}

}
