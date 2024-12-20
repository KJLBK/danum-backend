package com.danum.danum.domain.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Getter
    @Column(name = "member_name")
    private String name;

    @Column(name = "member_exp")
    private int exp;

    @Column(name = "member_contribution")
    private Integer contribution;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    @Column(name = "member_role")
    @Getter
    private Role role;

    @Column(name = "member_join")
    private LocalDateTime joinDateTime;

    @Getter
    @Column(name = "latitude")
    private Double latitude;

    @Getter
    @Column(name = "longitude")
    private Double longitude;

    @Getter
    @Column(name = "address")
    private String address;

    @Getter
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    public void updateUserPassword(String password) {
        this.password = password;
    }

    public void updateUserPhone(String phone) {
        this.phone = phone;
    }

    public void updateUserName(String username) {
        this.name = username;
    }

    public void updateUserAddress(String address) {
        this.address = address;
    }

    public void updateUserLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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
        return contribution != null && contribution == 0;
    }

    public void activate() {
        this.contribution = 0;
    }

    public void deactivate() {
        this.contribution = 1;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public Integer getContribution() {
        return this.contribution;
    }

    public int getExp() {
        return exp;
    }
    public void setExp(int exp) {
        this.exp = exp;
    }

    public void increaseExp() {
        this.exp = Math.min(this.exp + 1, 100);
    }


}