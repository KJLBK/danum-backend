package com.danum.danum.controller;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.service.board.QuestionService;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @GetMapping("/board/searchQuestion/{id}")
    public ResponseEntity<?> search(@PathVariable("id") int page){
        return ResponseEntity.ok(questionService.search(page));
    }

    @GetMapping("/board/oneSearchQuestion/{id}")
    public ResponseEntity<?> oneSearch(@PathVariable("id") Long id){
        return ResponseEntity.ok(questionService.oneSearch(id));
    }

    @GetMapping("/board/Question/like/{id}")
    public ResponseEntity<?> like(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementLikeCount(id));
    }

    @GetMapping("/board/Question/count/{id}")
    public ResponseEntity<?> count(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.incrementViewCount(id));
    }

}
