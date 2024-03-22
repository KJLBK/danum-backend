package com.danum.danum.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "members")
public class MemberDTO {
    @Id
    @Column(name = "member_email")
    String email;
    @Column(name = "member_password")
    String password;
    @Column(name = "member_phone")
    String phone;
}
