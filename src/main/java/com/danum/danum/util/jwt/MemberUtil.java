package com.danum.danum.util.jwt;

import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberUtil {

	private final MemberRepository memberRepository;

	public Member findById(String id) {
		Optional<Member> optionalMember = memberRepository.findById(id);

		return optionalMember.orElseThrow(() ->
			new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION)
			);
	}

}
