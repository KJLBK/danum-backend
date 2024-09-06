package com.danum.danum.controller.comment;

import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.service.comment.QuestionCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

}
