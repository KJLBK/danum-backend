package com.danum.danum.filter;

import com.danum.danum.exception.ErrorCode;
import com.danum.danum.util.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer";

	private static final Integer TOKEN_START_INDEX = 7;

	private final JwtUtil jwtUtil;

	@Value("${authentication.path.all}")
	private String[] allowedPaths;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String tokenHeader = request.getHeader("Authorization");
		String token = resolveToken(tokenHeader);
		validateToken(token);

		Authentication auth = jwtUtil.getAuthentication(token);
		SecurityContextHolder.getContext().setAuthentication(auth);

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return Arrays.stream(allowedPaths)
				.anyMatch(path::startsWith);
	}

	private void validateToken(String token) {
		jwtUtil.validate(token);
	}

	private String resolveToken(String tokenHeader) {
		if (!StringUtils.hasText(tokenHeader) ||
				tokenHeader.length() <= TOKEN_START_INDEX ||
				!tokenHeader.startsWith(PREFIX)) {
			throw new JwtException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION.getMessage());
		}

		return tokenHeader.substring(TOKEN_START_INDEX);
	}

}
