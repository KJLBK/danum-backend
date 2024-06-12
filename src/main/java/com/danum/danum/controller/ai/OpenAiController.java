package com.danum.danum.controller.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.domain.openai.OpenAiResponse;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.service.ai.OpenAiConversationService;
import com.danum.danum.service.ai.OpenAiMessageService;
import com.danum.danum.service.ai.OpenAiService;
import com.danum.danum.service.member.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-ai")
public class OpenAiController {

    private final OpenAiService openAiService;

    private final OpenAiConversationService openAiConversationService;

    private final OpenAiMessageService openAiMessageService;

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<OpenAiResponse> generate(@RequestBody OpenAiUserMessageDto message) {
        Member member = memberService.getMemberByAuthentication();
        OpenAiConversation conversation = openAiConversationService.loadProgressingConversation(member);

        ChatResponse chatResponse = openAiService.sendMessage(message,
                openAiMessageService.loadProgressingMessage(conversation));

        // 유저 메시지 저장
        openAiMessageService.saveMessage(message.getMessage(),
                conversation,
                MessageType.USER);

        // ai 메시지 저장
        String aiMessage = chatResponse.getResult()
                .getOutput()
                .getContent();

        openAiMessageService.saveMessage(aiMessage,
                conversation,
                MessageType.ASSISTANT);

        return ResponseEntity.ok()
                .body(OpenAiResponse.builder()
                        .createdId(conversation.getCreateId())
                        .message(aiMessage)
                        .build());
    }

    @GetMapping("/progressing/conversation")
    public ResponseEntity<OpenAiConversation> loadProgressingConversation() {
        Member member = memberService.getMemberByAuthentication();

        return ResponseEntity.ok()
                .body(openAiConversationService.loadProgressingConversation(member));
    }

    @GetMapping("/progressing/message")
    public ResponseEntity<List<OpenAiMessage>> loadProgressingMessage() {
        Member member = memberService.getMemberByAuthentication();
        OpenAiConversation conversation = openAiConversationService.loadProgressingConversation(member);

        return ResponseEntity.ok()
                .body(openAiMessageService.loadProgressingMessage(conversation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OpenAiMessage>> loadMessage(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(openAiMessageService.loadMessageByConversation(
                        openAiConversationService.loadConversation(id)));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> close(@PathVariable("id") Long id) {
        openAiConversationService.conversationClosed(id);

        return ResponseEntity.ok()
                .build();
    }

}
