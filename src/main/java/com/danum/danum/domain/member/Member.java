package com.danum.danum.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "members")
public class Member {

    @Id
    @Column(name = "member_email")
    private String email;

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

    public void updateUserPassword(String password){
        this.password = password;
    }

    public void updateUserPhone(String phone){
        this.phone = phone;
    }

    public void updateUserName(String username){
        this.name = username;
    }

    public String getPassword(){
        return this.password;
    }

}
