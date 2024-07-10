package com.danum.danum.controller.board;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.service.board.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/new")
    public ResponseEntity<?> createQuestionBoard(@RequestBody QuestionNewDto questionNewDto){
        questionService.create(questionNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/show")
    public ResponseEntity<?> getQuestionBoardList(){
        return ResponseEntity.ok(questionService.viewList());
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getQuestionBoardById(@PathVariable("id") Long id){
        return ResponseEntity.ok(questionService.view(id, getLoginUser()));
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeStatus(@PathVariable("id") Long id) {
        questionService.likeStatus(id, getLoginUser());

        return ResponseEntity.ok("좋아요 관련 성공");
    }

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

}
