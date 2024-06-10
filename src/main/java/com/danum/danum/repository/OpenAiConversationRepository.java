package com.danum.danum.repository;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiConversationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenAiConversationRepository extends JpaRepository<OpenAiConversation, Long> {

	List<OpenAiConversation> findByMemberAndStatus(Member member, OpenAiConversationStatus status);

}
