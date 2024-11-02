package com.danum.danum.controller.board;

import com.danum.danum.domain.board.page.PagedResponseDto;
import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionUpdateDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.board.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question")
public class QuestionController {

    private final QuestionService questionService;
    private final AdminService adminService;

    @PostMapping("/new")
    public ResponseEntity<?> createQuestionBoard(@RequestBody QuestionNewDto questionNewDto) {
        questionService.create(questionNewDto);
        return ResponseEntity.ok("게시판 생성 성공");
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