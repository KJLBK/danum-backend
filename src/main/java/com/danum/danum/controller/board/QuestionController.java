package com.danum.danum.controller.board;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionUpdateDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.board.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/question")
public class QuestionController {

    private final QuestionService questionService;
    private final AdminService adminService;

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

    @PutMapping("/update")
    public ResponseEntity<?> updateQuestionBoard(@RequestBody QuestionUpdateDto questionUpdateDto) {
        questionService.update(questionUpdateDto, getLoginUser());

        return ResponseEntity.ok("게시판 수정 성공");
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("질문이 삭제되었습니다.");
    }

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members/{email}/questions")
    public ResponseEntity<List<QuestionViewDto>> getMemberQuestions(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberQuestions(email));
    }
}
