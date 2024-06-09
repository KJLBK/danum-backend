package com.danum.danum.controller.board;

import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.service.comment.QuestionCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question/comment")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;

    @PostMapping("/new")
    public ResponseEntity<?> created(@RequestBody QuestionCommentNewDto questionCommentNewDto) {
        questionCommentService.created(questionCommentNewDto);

        return ResponseEntity.ok("댓글 생성 성공");
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> viewList(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionCommentService.viewList(id));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody QuestionCommentUpdateDto questionCommentUpdateDto) {
        questionCommentService.update(questionCommentUpdateDto);

        return ResponseEntity.ok("댓글 수정 성공");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        questionCommentService.delete(id);

        return ResponseEntity.ok("댓글 삭제 성공");
    }

}