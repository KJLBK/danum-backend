package com.danum.danum.controller.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import com.danum.danum.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 모든 회원 조회
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    // 특정 회원 조회
    @GetMapping("/members/{email}")
    public ResponseEntity<Member> getMemberByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberByEmail(email));
    }

    // 회원의 질문 조회
    @GetMapping("/members/{email}/questions")
    public ResponseEntity<List<QuestionViewDto>> getMemberQuestions(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberQuestions(email));
    }

    // 회원의 마을 게시글 조회
    @GetMapping("/members/{email}/villages")
    public ResponseEntity<List<VillageViewDto>> getMemberVillages(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberVillages(email));
    }

    // 회원의 댓글 조회
    @GetMapping("/members/{email}/comments")
    public ResponseEntity<Map<String, List<?>>> getMemberComments(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberComments(email));
    }

    // 회원 활성화
    @PostMapping("/members/{email}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable("email") String email) {
        adminService.activateMember(email);
        return ResponseEntity.ok().build();
    }

    // 회원 비활성화
    @PostMapping("/members/{email}/deactivate")
    public ResponseEntity<Void> deactivateMember(@PathVariable("email") String email) {
        adminService.deactivateMember(email);
        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping("/members/{email}")
    public ResponseEntity<Void> deleteMember(@PathVariable("email") String email) {
        adminService.deleteMember(email);
        return ResponseEntity.ok().build();
    }

    // 모든 질문 조회
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionViewDto>> getAllQuestions() {
        return ResponseEntity.ok(adminService.getAllQuestionViews());
    }

    // 질문 삭제
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long id) {
        adminService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    // 모든 마을 게시글 조회
    @GetMapping("/villages")
    public ResponseEntity<List<VillageViewDto>> getAllVillages() {
        return ResponseEntity.ok(adminService.getAllVillageViews());
    }

    // 마을 게시글 삭제
    @DeleteMapping("/villages/{id}")
    public ResponseEntity<Void> deleteVillage(@PathVariable("id") Long id) {
        adminService.deleteVillage(id);
        return ResponseEntity.ok().build();
    }

    // 질문 댓글 삭제
    @DeleteMapping("/questions/{questionId}/comments/{commentId}")
    public ResponseEntity<Void> deleteQuestionComment(
            @PathVariable("questionId") Long questionId,
            @PathVariable("commentId") Long commentId) {
        adminService.deleteQuestionComment(questionId, commentId);
        return ResponseEntity.ok().build();
    }

    // 마을 게시글 댓글 삭제
    @DeleteMapping("/villages/{villageId}/comments/{commentId}")
    public ResponseEntity<Void> deleteVillageComment(
            @PathVariable("villageId") Long villageId,
            @PathVariable("commentId") Long commentId) {
        adminService.deleteVillageComment(villageId, commentId);
        return ResponseEntity.ok().build();
    }

    // 질문 상세 조회 (추가된 부분)
    @GetMapping("/questions/{id}")
    public ResponseEntity<QuestionViewDto> getQuestionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getQuestionView(id));
    }

    @GetMapping("/village/{id}")
    public ResponseEntity<VillageViewDto> getVillageById(@PathVariable("id") Long id){
        return ResponseEntity.ok(adminService.getVillageView(id));
    }

    //질문게시글안에 있는 댓글 삭제
    @GetMapping("/questions/{questionId}/comments")
    public ResponseEntity<List<QuestionComment>> getQuestionComments(@PathVariable Long questionId) {
        List<QuestionComment> comments = adminService.getQuestionComments(questionId);
        return ResponseEntity.ok(comments);
    }

    //마을게시글안에 있는 댓글 삭제
    @GetMapping("/villages/{villageId}/comments")
    public ResponseEntity<List<VillageComment>> getVillageComments(@PathVariable Long villageId) {
        List<VillageComment> comments = adminService.getVillageComments(villageId);
        return ResponseEntity.ok(comments);
    }

}