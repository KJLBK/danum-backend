package com.danum.danum.controller.board;

import com.danum.danum.domain.board.page.PagedResponseDto;
import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionUpdateDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiResponse;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.ai.OpenAiConversationService;
import com.danum.danum.service.ai.OpenAiMessageService;
import com.danum.danum.service.ai.OpenAiService;
import com.danum.danum.service.board.question.QuestionService;
import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question")
public class QuestionController {

    private final QuestionService questionService;
    private final AdminService adminService;
    private final OpenAiService openAiService;
    private final OpenAiConversationService openAiConversationService;
    private final OpenAiMessageService openAiMessageService;
    private final MemberService memberService;

    @PostMapping("/new")
    public ResponseEntity<?> createQuestionBoard(@RequestBody QuestionNewDto questionNewDto) {
        // GPT 대화 생성 및 응답 받기
        Member member = memberService.getMemberByAuthentication();
        OpenAiConversation conversation = openAiConversationService.loadProgressingConversation(member);

        // GPT에게 질문 전송
        ChatResponse chatResponse = openAiService.sendMessage(
                new OpenAiUserMessageDto() {{
                    setMessage(questionNewDto.getContent());
                }},
                openAiMessageService.loadProgressingMessage(conversation)
        );

        // GPT 응답 저장
        String aiMessage = chatResponse.getResult().getOutput().getContent();
        openAiMessageService.saveMessage(
                questionNewDto.getContent(),
                conversation,
                MessageType.USER
        );
        openAiMessageService.saveMessage(
                aiMessage,
                conversation,
                MessageType.ASSISTANT
        );

        // 게시글 내용에 AI 응답 추가
        String combinedContent = questionNewDto.getContent() + "\n\n[AI 답변]\n" + aiMessage;
        questionNewDto.setContent(combinedContent);
        questionNewDto.setCreateId(conversation.getCreateId());

        // Question 생성
        QuestionViewDto createdQuestion = questionService.create(questionNewDto);

        return ResponseEntity.ok()
                .body(Map.of(
                        "questionId", createdQuestion.getQuestion_id(),
                        "conversationId", conversation.getCreateId(),
                        "message", "게시글이 생성되었습니다. AI와의 대화를 계속하실 수 있습니다."
                ));
    }

    // AI와 추가 대화를 위한 엔드포인트 추가
    @PostMapping("/{questionId}/ai-chat")
    public ResponseEntity<?> continueAiChat(
            @PathVariable Long questionId,
            @RequestBody OpenAiUserMessageDto messageDto
    ) {
        QuestionViewDto question = questionService.view(questionId, getCurrentUserEmail());

        OpenAiConversation conversation = openAiConversationService.loadConversation(question.getCreateId());

        ChatResponse chatResponse = openAiService.sendMessage(
                messageDto,
                openAiMessageService.loadProgressingMessage(conversation)
        );

        String aiMessage = chatResponse.getResult().getOutput().getContent();
        openAiMessageService.saveMessage(messageDto.getMessage(), conversation, MessageType.USER);
        openAiMessageService.saveMessage(aiMessage, conversation, MessageType.ASSISTANT);

        // 추가 질문과 답변을 게시글에 업데이트
        String updatedContent = question.getContent() + "\n\n[추가 질문]\n" + messageDto.getMessage() + "\n\n[AI 답변]\n" + aiMessage;
        QuestionUpdateDto updateDto = new QuestionUpdateDto(questionId, question.getTitle(), updatedContent);

        questionService.update(updateDto, getCurrentUserEmail());

        return ResponseEntity.ok()
                .body(Map.of(
                        "message", aiMessage,
                        "questionId", questionId,
                        "conversationId", conversation.getCreateId()
                ));
    }

    @GetMapping("/show")
    public ResponseEntity<PagedResponseDto<QuestionViewDto>> getQuestionBoardList(@PageableDefault(size = 10) Pageable pageable) {
        Page<QuestionViewDto> questionPage = questionService.viewList(pageable);
        return ResponseEntity.ok(PagedResponseDto.from(questionPage));
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getQuestionBoardById(@PathVariable("id") Long id) {
        QuestionViewDto question = questionService.view(id, getCurrentUserEmail());
        return ResponseEntity.ok(question);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeStatus(@PathVariable("id") Long id) {
        questionService.likeStatus(id, getCurrentUserEmail());
        return ResponseEntity.ok("좋아요 관련 성공");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuestionBoard(@RequestBody QuestionUpdateDto questionUpdateDto) {
        validateUpdateRequest(questionUpdateDto);
        questionService.update(questionUpdateDto, getCurrentUserEmail());
        return ResponseEntity.ok("게시판 수정 성공");
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("질문이 삭제되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members/{email}/questions")
    public ResponseEntity<PagedResponseDto<QuestionViewDto>> getMemberQuestions(
            @PathVariable("email") String email,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<QuestionViewDto> questions = adminService.getMemberQuestions(email, pageable);
        return ResponseEntity.ok(PagedResponseDto.from(questions));
    }

    @GetMapping("/{id}/has-accepted-comment")
    public ResponseEntity<Boolean> hasAcceptedComment(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.hasAcceptedComment(id));
    }

    @GetMapping("/region")
    public ResponseEntity<PagedResponseDto<QuestionViewDto>> getQuestionsByRegion(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<QuestionViewDto> questionPage = questionService.getQuestionsByRegion(city, district, pageable);
        return ResponseEntity.ok(PagedResponseDto.from(questionPage));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private void validateUpdateRequest(QuestionUpdateDto updateDto) {
        if (updateDto == null || updateDto.getId() == null) {
            throw new IllegalArgumentException("올바르지 않은 수정 요청입니다.");
        }
    }
}