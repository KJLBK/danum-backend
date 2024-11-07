package com.danum.danum.handler;

import com.danum.danum.exception.CustomException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.util.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.debug("CONNECT with token: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    jwtUtil.validate(token);
                    Authentication authentication = jwtUtil.getAuthentication(token);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);

                    log.debug("Successfully authenticated WebSocket connection for user: {}",
                            authentication.getName());
                    return message;
                } catch (ExpiredJwtException e) {
                    log.error("JWT 토큰이 만료되었습니다: {}", token);
                    throw new CustomException(ErrorCode.TOKEN_EXPIRED_EXCEPTION);
                } catch (SignatureException e) {
                    log.error("JWT 토큰 서명 확인 중 오류가 발생했습니다: {}", token);
                    throw new CustomException(ErrorCode.TOKEN_SIGNATURE_EXCEPTION);
                } catch (Exception e) {
                    log.error("JWT 토큰 검증 중 오류가 발생했습니다: {}", token);
                    throw new CustomException(ErrorCode.TOKEN_ROLE_NOT_AVAILABLE_EXCEPTION);
                }
            }
            log.error("JWT 토큰이 없거나 잘못된 형식입니다.");
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION);
        }
        else if (StompCommand.SEND.equals(accessor.getCommand())) {
            Principal user = accessor.getUser();
            if (user == null) {
                // 헤더에서 토큰 재확인
                String jwtToken = accessor.getFirstNativeHeader("Authorization");
                if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                    try {
                        String token = jwtToken.substring(7);
                        jwtUtil.validate(token);
                        Authentication authentication = jwtUtil.getAuthentication(token);
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Re-authenticated user from token: {}", authentication.getName());
                    } catch (Exception e) {
                        log.error("Failed to re-authenticate user from token", e);
                        throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION);
                    }
                } else {
                    log.error("No authentication found for message sending and no valid token provided");
                    throw new CustomException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION);
                }
            } else {
                log.debug("Message sending authenticated for user: {}", user.getName());
                // 필요한 경우 SecurityContext 업데이트
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                if (existingAuth == null && user instanceof Authentication) {
                    SecurityContextHolder.getContext().setAuthentication((Authentication) user);
                }
            }
        }

        return message;
    }
}