package com.danum.danum.domain.openai;

import com.danum.danum.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiConversation {


	@Id @Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long createId;

	@ManyToOne
	private Member member;

	private OpenAiConversationStatus status;

	public boolean isClosed() {
		return status == OpenAiConversationStatus.CLOSED;
	}

	public void conversationClose() {
		this.status = OpenAiConversationStatus.CLOSED;
	}

}
