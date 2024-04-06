package com.danum.danum.controller;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.service.board.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/board/newQuestion")
    public ResponseEntity<?> created(@RequestBody QuestionNewDto questionNewDto){
        return ResponseEntity.ok(questionService.created(questionNewDto));
    }

    @GetMapping("/board/serachQuestion")
    public ResponseEntity<?> search(){
        return ResponseEntity.ok(questionService.search());
    }

}
