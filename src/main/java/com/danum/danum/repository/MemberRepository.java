package com.danum.danum.repository;

import com.danum.danum.domain.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberDTO, String> {
    public Optional<MemberDTO> findByEmail(String email);
}
