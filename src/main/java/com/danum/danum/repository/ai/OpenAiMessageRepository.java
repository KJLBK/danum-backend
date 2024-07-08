package com.danum.danum.repository.ai;

import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenAiMessageRepository extends JpaRepository<OpenAiMessage, Long> {

    List<OpenAiMessage> findByOpenAiConversation(OpenAiConversation openAiConversation);

}
