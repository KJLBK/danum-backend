package com.danum.danum.util.jwt;

import com.danum.danum.domain.jwt.TokenBox;
import com.danum.danum.domain.jwt.TokenDto;
import com.danum.danum.domain.jwt.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

	private Key key;

	@Value("${jwt.expired-time.access-token}")
	private Long accessTokenExpiredTime;

	@Value("${jwt.expired-time.refresh-token}")
	private Long refreshTokenExpiredTime;

	public JwtUtil(@Value("${jwt.secret-key}") String key) {
		byte[] keyBytes = Decoders.BASE64.decode(key);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public TokenBox generateToken(Authentication authentication) {
		long now = (new Date()).getTime();

		Date accessTokenExpired = new Date(now + accessTokenExpiredTime);
		Date refreshTokenExpired = new Date(now + refreshTokenExpiredTime);

		String accessToken = getNewToken(accessTokenExpired, authentication);
		String refreshToken = getNewToken(refreshTokenExpired, authentication);

		TokenDto accessTokenDto = new TokenDto(TokenType.ACCESS, accessToken);
		TokenDto refreshTokenDto = new TokenDto(TokenType.REFRESH, refreshToken);

		return new TokenBox(accessTokenDto, refreshTokenDto);
	}

	private String getNewToken(Date tokenExpiredTime, Authentication authentication) {
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim("auth", authentication.getAuthorities())
				.signWith(key, SignatureAlgorithm.HS256)
				.setExpiration(tokenExpiredTime)
				.compact();
	}

	public void validate(String token) {
		try {
			log.info("JWT validate...");
			Jws<Claims> claims = Jwts.parserBuilder()
					.setSigningKey(this.key)
					.build()
					.parseClaimsJws(token);

			log.info("{}", claims.getBody()
					.getExpiration());

			if (!claims.getBody()
					.getExpiration()
					.before(new Date())) {
				throw new JwtException("만료된 토큰입니다.");
			}
		} catch(Exception e) {
			log.error("Token not available");
		}
	}

}
