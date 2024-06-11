package com.danum.danum.handler;

import com.danum.danum.exception.CustomJwtException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {


    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("CONNECT {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    jwtUtil.validate(token);
                    Authentication authentication = jwtUtil.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (ExpiredJwtException e) {
                    log.error("JWT 토큰이 만료되었습니다: {}", token);
                    throw new CustomJwtException(ErrorCode.TOKEN_EXPIRED_EXCEPTION);
                } catch (SignatureException e) {
                    log.error("JWT 토큰 서명 확인 중 오류가 발생했습니다: {}", token);
                    throw new CustomJwtException(ErrorCode.TOKEN_SIGNATURE_EXCEPTION);
                }
            } else {
                log.info("JWT 토큰이 없거나 잘못된 형식입니다.");
                throw new CustomJwtException(ErrorCode.TOKEN_NOT_FOUND_EXCEPTION);
            }
        }

        return message;
    }
}