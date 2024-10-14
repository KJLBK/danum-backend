package com.danum.danum.controller.comment;

import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.comment.QuestionCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question/comment")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    private final AdminService adminService;

    @PostMapping("/new")
    public ResponseEntity<?> createQuestionBoardComment(@RequestBody QuestionCommentNewDto questionCommentNewDto) {
        questionCommentService.create(questionCommentNewDto);

        return ResponseEntity.ok("댓글 생성 성공");
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getQuestionBoardForCommentList(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionCommentService.viewList(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuestionBoardComment(@RequestBody QuestionCommentUpdateDto questionCommentUpdateDto) {

        questionCommentService.update(questionCommentUpdateDto, getLoginUser());

        return ResponseEntity.ok("댓글 수정 성공");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestionBoardComment(@PathVariable("id") Long id) {
        questionCommentService.delete(id, getLoginUser());

        return ResponseEntity.ok("댓글 삭제 성공");
    }

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

    @PostMapping("/{questionId}/accept/{commentId}")
    public ResponseEntity<?> acceptQuestionComment(@PathVariable Long questionId, @PathVariable Long commentId) {
        questionCommentService.acceptComment(questionId, commentId, getLoginUser());
        return ResponseEntity.ok("해당 답변을 채택하였습니다.");
    }

    @PostMapping("/{questionId}/unaccept/{commentId}")
    public ResponseEntity<?> unacceptQuestionComment(@PathVariable Long questionId, @PathVariable Long commentId) {
        questionCommentService.unacceptComment(questionId, commentId, getLoginUser());
        return ResponseEntity.ok("해당 답변 채택이 취소하였습니다.");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/questions/{questionId}/comments/{commentId}")
    public ResponseEntity<Void> deleteQuestionComment(
            @PathVariable("questionId") Long questionId,
            @PathVariable("commentId") Long commentId) {
        adminService.deleteQuestionComment(questionId, commentId);
        return ResponseEntity.ok().build();
    }
}
