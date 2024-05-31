package com.danum.danum.domain.member;

import com.danum.danum.domain.board.Board;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member {

    @Id
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
    private Role role;

    @Column(name = "member_join")
    private LocalDateTime joinDateTime;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Board> questions = new ArrayList<>();

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

}
