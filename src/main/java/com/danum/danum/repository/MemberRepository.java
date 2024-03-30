package com.danum.danum.repository;

import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    public Optional<Member> findByEmail(String email);

    public Optional<Member> findByName(String name);

}
