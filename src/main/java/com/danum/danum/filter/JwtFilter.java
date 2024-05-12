package com.danum.danum.filter;

import com.danum.danum.exception.ErrorCode;
import com.danum.danum.util.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String PREFIX = "Bearer";

	private static final Integer TOKEN_START_INDEX = 7;

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String tokenHeader = request.getHeader("Authorization");
		String token = resolveToken(tokenHeader);
		validateToken(token);

		filterChain.doFilter(request, response);
	}

	private void validateToken(String token) {
		jwtUtil.validate(token);
	}

	private String resolveToken(String tokenHeader) {
		if (tokenHeader == null ||
				tokenHeader.length() <= TOKEN_START_INDEX ||
				StringUtils.hasText(tokenHeader) ||
				!tokenHeader.startsWith(PREFIX)) {
			throw new JwtException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION.getMessage());
		}

		return tokenHeader.substring(TOKEN_START_INDEX);
	}

}
