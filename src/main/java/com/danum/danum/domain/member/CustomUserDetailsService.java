package com.danum.danum.domain.member;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return new CustomUserDetails(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else if ("user".equals(username)) {
            return new CustomUserDetails(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // 모든 사용자 정보를 출력하는 메서드
    public void printAllUserDetails() {
        UserDetails adminDetails = loadUserByUsername("admin");
        UserDetails userDetails = loadUserByUsername("user");

        System.out.println("Admin Details: ");
        System.out.println("Username: " + adminDetails.getUsername());
        System.out.println("Password: " + adminDetails.getPassword());
        System.out.println("Authorities: " + adminDetails.getAuthorities());

        System.out.println("\nUser Details: ");
        System.out.println("Username: " + userDetails.getUsername());
        System.out.println("Password: " + userDetails.getPassword());
        System.out.println("Authorities: " + userDetails.getAuthorities());
    }
}
