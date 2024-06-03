package com.danum.danum.controller.board;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.service.board.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/board/question/new")
    public ResponseEntity<?> created(@RequestBody QuestionNewDto questionNewDto){
        questionService.created(questionNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/board/question/view/list")
    public ResponseEntity<?> viewList(){
        return ResponseEntity.ok(questionService.viewList());
    }

    @PostMapping("/board/question/view/{id}")
    public ResponseEntity<?> view(@PathVariable("id") Long id){
        return ResponseEntity.ok(questionService.view(id));
    }

    @PostMapping("/board/question/{id}/like")
    public ResponseEntity<?> like(@PathVariable("id") Long id) {
        boolean check = questionService.updateLike(id);
        if (check) {
            return ResponseEntity.ok("좋아요 성공");
        }

        return ResponseEntity.ok("좋아요 취소");
    }

}
