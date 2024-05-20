package com.danum.danum.controller;

import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearchDto;
import com.danum.danum.domain.board.QuestionViewDto;
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

    @PostMapping("/board/new")
    public ResponseEntity<?> created(@RequestBody QuestionNewDto questionNewDto){
        questionService.created(questionNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/board/viewlist")
    public ResponseEntity<?> boardViewList(@RequestBody QuestionViewDto questionView){
        return ResponseEntity.ok(questionService.boardViewList(questionView));
    }

    @GetMapping("/board/view/{id}")
    public ResponseEntity<?> boardView(@PathVariable("id") Long id){
        return ResponseEntity.ok(questionService.boardView(id));
    }

    @GetMapping("/board/search")
    public ResponseEntity<?> searchList(@RequestBody QuestionSearchDto questionSearch){
        return ResponseEntity.ok(questionService.boardSearchList(questionSearch));
    }

    @PostMapping("/board/like/{id}")
    public ResponseEntity<?> increaseLikes(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementLikeCount(id));
    }

    @PostMapping("/board/count/{id}")
    public ResponseEntity<?> increaseViews(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementViewCount(id));
    }

}
