package com.danum.danum.controller;

import com.danum.danum.domain.comment.CommentDeleteDto;
import com.danum.danum.domain.comment.CommentNewDto;
import com.danum.danum.domain.comment.CommentUpdateDto;
import com.danum.danum.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/new")
    public ResponseEntity<?> created(@RequestBody CommentNewDto commentNewDto){
        commentService.created(commentNewDto);

        return ResponseEntity.ok("댓글 생성 성공");
    }

    @GetMapping("/comment/view/{id}")
    public ResponseEntity<?> commentView(@PathVariable("id") Long id){
        return ResponseEntity.ok(commentService.commentView(id));
    }

    @PatchMapping("/comment/update")
    public ResponseEntity<?> commentUpdate(@RequestBody CommentUpdateDto commentUpdateDto){
        commentService.update(commentUpdateDto);

        return ResponseEntity.ok("댓글 수정 성공");
    }

    @DeleteMapping("/comment/delete")
    public ResponseEntity<?> commentDelete(@RequestBody CommentDeleteDto commentDeleteDto){
        commentService.delete(commentDeleteDto);

        return ResponseEntity.ok("댓글 삭제 성공");
    }

}
