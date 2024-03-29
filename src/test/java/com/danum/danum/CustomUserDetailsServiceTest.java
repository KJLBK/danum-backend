package com.danum.danum;

import com.danum.danum.domain.member.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void loadUserByUsername_WhenUserExists() {
        // "admin" 사용자가 존재한다고 가정
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        // 실제 비밀번호 확인은 생략 (암호화되어있기 때문)
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    public void loadUserByUsername_WhenUserDoesNotExist() {
        // 존재하지 않는 사용자 조회
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
