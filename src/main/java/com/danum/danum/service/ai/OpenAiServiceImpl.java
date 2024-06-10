package com.danum.danum.service.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiConversationStatus;
import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.OpenAiException;
import com.danum.danum.repository.OpenAiConversationRepository;
import com.danum.danum.repository.OpenAiMessageRepository;
import com.danum.danum.service.member.MemberService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.FunctionMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiConversationRepository openAiConversationRepository;

    private final OpenAiMessageRepository openAiMessageRepository;

    private final MemberService memberService;

    private final ChatModel chatModel;

    @Override
    @Transactional
    public synchronized ChatResponse sendMessage(final OpenAiUserMessageDto messageDto) {
        String message = messageDto.getMessage();
        Member member = memberService.getMemberByAuthentication();

        // 진행중 상태의 사용자 대화(2개 이상 불가능)
        List<OpenAiConversation> conversationList = openAiConversationRepository.findByMemberAndStatus(member,
                OpenAiConversationStatus.PROCESSING);
        OpenAiConversation conversation = getConversation(member, conversationList);

        // 대화의 세부 메시지 목록
        List<OpenAiMessage> messageList = openAiMessageRepository.findByOpenAiConversation(conversation);

        // 프롬프트용 메시지 기록에 사용자 메시지 추가
        List<Message> promptMessage = convertMessageList(messageList);
        promptMessage.add(new UserMessage(message));

        // 프롬프트 작성 및 api 응답 반환
        Prompt prompt = new Prompt(promptMessage);
        ChatResponse response = ChatClient.create(chatModel)
                .prompt(prompt)
                .call()
                .chatResponse();

        // 사용자 메시지 DB에 저장
        messageSave(message,
                response.getResult()
                        .getOutput()
                        .getContent(),
                conversation);

        return response;
    }

    private OpenAiConversation getConversation(Member member, List<OpenAiConversation> conversationList) {
        if (conversationList.isEmpty()) {
            return generateConversation(member);
        }

        if (conversationList.size() > 1) {
            throw new OpenAiException(ErrorCode.MULTIPLE_PROCESSING_MESSAGE_EXCEPTION);
        }

        return conversationList.get(0);
    }

    private OpenAiConversation generateConversation(Member member) {
        return openAiConversationRepository.save(OpenAiConversation.builder()
                .member(member)
                .status(OpenAiConversationStatus.PROCESSING)
                .build());
    }

    private void messageSave(String sendMessage, String receiveMessage, OpenAiConversation conversation) {
        LocalDateTime now = LocalDateTime.now();

        // 사용자 메시지 DB에 저장
        openAiMessageRepository.save(OpenAiMessage.builder()
                .createTime(now)
                .openAiConversation(conversation)
                .messageType(MessageType.USER)
                .content(sendMessage)
                .build()
        );

        // Ai 메시지 DB에 저장
        openAiMessageRepository.save(OpenAiMessage.builder()
                .createTime(now)
                .openAiConversation(conversation)
                .messageType(MessageType.ASSISTANT)
                .content(receiveMessage)
                .build()
        );
    }

    private List<Message> convertMessageList(List<OpenAiMessage> messageList) {
        return messageList.stream()
                .map(conversation -> {
                    String content = conversation.getContent();

                    return switch (conversation.getMessageType()) {
                        case ASSISTANT -> new AssistantMessage(content);
                        case USER -> new UserMessage(content);
                        case SYSTEM -> new SystemMessage(content);
                        case FUNCTION -> new FunctionMessage(content);
                        default -> throw new OpenAiException(ErrorCode.MESSAGE_TYPE_NOT_SUPPORTED_EXCEPTION);
                    };
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void conversationClosed(Long conversationId) {
        Optional<OpenAiConversation> OptionalConversation = openAiConversationRepository.findById(conversationId);
        if (OptionalConversation.isEmpty()) {
            throw new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION);
        }

        OpenAiConversation conversation = OptionalConversation.get();
        conversation.conversationClose();
    }

}
