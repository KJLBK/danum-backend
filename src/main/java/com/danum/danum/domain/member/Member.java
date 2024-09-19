package com.danum.danum.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member {

    @Id
    @Getter
    @Column(name = "member_email")
    private String email;

    @Getter
    @Column(name = "member_password")
    private String password;

    @Column(name = "member_phone")
    private String phone;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_exp")
    private int exp;

    @Column(name = "member_contribution")
    private int contribution;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    @Column(name = "member_role")
    @Getter
    private Role role;

    @Column(name = "member_join")
    private LocalDateTime joinDateTime;

    @Getter
    @Column(name = "latitude") //사용자의 위도
    private Double latitude;

    @Getter
    @Column(name ="longitude")// 사용자의 경도
    private Double longitude;

    public void updateUserPassword(String password){
        this.password = password;
    }

    public void updateUserPhone(String phone){
        this.phone = phone;
    }

    public void updateUserName(String username){
        this.name = username;
    }

    public UserDetails mappingUserDetails() {
        Collection<? extends GrantedAuthority> auth = Stream.of(this.role.toString())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.builder()
                .username(this.email)
                .password(this.password)
                .authorities(auth)
                .build();
    }
    public boolean isActive() {
        return contribution == 0;
    }  //회원 기본 상태

    public void activate() {
        this.contribution = 0;
    } // 회원 활동 중

    public void deactivate() {
        this.contribution = 1;
    } // 회원 정지 중

    public void setContribution(int contribution) {
        this.contribution = contribution;
    }

}
