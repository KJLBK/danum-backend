package com.danum.danum.domain.openai;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne
    private OpenAiConversation openAiConversation;

    @Getter
    private String content;

    private LocalDateTime createTime;

}
