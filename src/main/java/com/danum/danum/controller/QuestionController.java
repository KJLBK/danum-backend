package com.danum.danum.controller;

import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearch;
import com.danum.danum.domain.board.QuestionView;
import com.danum.danum.service.board.QuestionService;
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

    @PostMapping("/board/newQuestion")
    public ResponseEntity<?> created(@RequestBody QuestionNewDto questionNewDto){
        questionService.created(questionNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/board/boardViewList")
    public ResponseEntity<?> boardViewList(@RequestBody QuestionView questionView){
        System.out.println(questionView.getCategory());
        return ResponseEntity.ok(questionService.boardViewList(questionView));
    }

    @GetMapping("/board/boardView/{id}")
    public ResponseEntity<?> boardView(@PathVariable("id") Long id){
        return ResponseEntity.ok(questionService.boardView(id));
    }

    @GetMapping("/board/boardSearch")
    public ResponseEntity<?> searchList(@RequestBody QuestionSearch questionSearch){
        return ResponseEntity.ok(questionService.boardSearchList(questionSearch));
    }

    @PostMapping("/board/Question/like/{id}")
    public ResponseEntity<?> like(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementLikeCount(id));
    }

    @PostMapping("/board/Question/count/{id}")
    public ResponseEntity<?> count(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementViewCount(id));
    }

}
