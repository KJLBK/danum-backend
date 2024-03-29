package com.danum.danum.repository;

import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

	@Query("SELECT m FROM Member m WHERE m.name = :name")
	Optional<Member> findByName(@Param("name") String name);

}
