package com.danum.danum.service.member;

import com.danum.danum.domain.member.Member;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findById(username);

        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        return member.mappingUserDetails();
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
