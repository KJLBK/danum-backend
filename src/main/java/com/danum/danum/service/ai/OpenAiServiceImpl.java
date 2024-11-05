package com.danum.danum.service.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.OpenAiException;
import com.danum.danum.repository.ai.OpenAiConversationRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.FunctionMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiConversationRepository openAiConversationRepository;

    private final ChatModel chatModel;

    private final MemberService memberService;

    @Override
    public ChatResponse sendMessage(final OpenAiUserMessageDto messageDto,
                                                 final List<OpenAiMessage> messageList) {
        // 프롬프트용 메시지 기록에 사용자 메시지 추가
        List<Message> promptMessage = convertMessageList(messageList);
        promptMessage.add(new UserMessage(messageDto.getMessage()));

        // 프롬프트에 사용자 위치 정보 추가
        Member member = memberService.getMemberByAuthentication();
        String userLocation = String.format("사용자의 위치: 위도 %f 경도 %f 이며 주소는 %s 입니다. 이 정보를 기반으로 한국어로 답변을 제공합니다." +
                        "다만, 사용자가 위치와 관련된 질문을 할 때만 이 위치 정보를 활용하여 답변하고, 위치 관련이 아닌 질문에는 위치 정보를 사용하지 않습니다. 또한 개인정보 보호를 위해 정확한 주소를 언급하지 않고, 지역 정보만 활용해 답변합니다.",
                member.getLatitude(), member.getLongitude(), member.getAddress());
        promptMessage.add(new UserMessage(userLocation));

        // 프롬프트 작성 및 api 응답 반환
        Prompt prompt = new Prompt(promptMessage);
        return ChatClient.create(chatModel)
                .prompt(prompt)
                .call()
                .chatResponse();
    }

    private List<Message> convertMessageList(final List<OpenAiMessage> messageList) {
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

}
