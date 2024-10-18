package com.danum.danum.controller.comment;

import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.comment.VillageCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/village/comment")
public class VillageCommentController {

    private final VillageCommentService villageCommentService;
    private final AdminService adminService;

    @PostMapping("/new")
    public ResponseEntity<?> createVillageBoardComment(@RequestBody VillageCommentNewDto villageCommentNewDto) {
        villageCommentService.create(villageCommentNewDto);
        return ResponseEntity.ok("댓글 생성 성공");
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getVillageBoardForCommentList(@PathVariable("id") Long id) {
        return ResponseEntity.ok(villageCommentService.viewList(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateVillageBoardComment(@RequestBody VillageCommentUpdateDto villageCommentUpdateDto) {
        villageCommentService.update(villageCommentUpdateDto, getLoginUser());
        return ResponseEntity.ok("댓글 수정 성공");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVillageBoardComment(@PathVariable("id") Long id) {
        villageCommentService.delete(id, getLoginUser());
        return ResponseEntity.ok("댓글 삭제 성공");
    }

    @PostMapping("/{villageId}/accept/{commentId}")
    public ResponseEntity<?> acceptVillageComment(@PathVariable Long villageId, @PathVariable Long commentId) {
        villageCommentService.acceptComment(villageId, commentId, getLoginUser());
        return ResponseEntity.ok("해당 답변을 채택하였습니다.");
    }

    @PostMapping("/{villageId}/unaccept/{commentId}")
    public ResponseEntity<?> unacceptVillageComment(@PathVariable Long villageId, @PathVariable Long commentId) {
        villageCommentService.unacceptComment(villageId, commentId, getLoginUser());
        return ResponseEntity.ok("해당 답변 채택을 취소하였습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/villages/{villageId}/comments/{commentId}")
    public ResponseEntity<Void> deleteVillageComment(
            @PathVariable("villageId") Long villageId,
            @PathVariable("commentId") Long commentId) {
        adminService.deleteVillageComment(villageId, commentId);
        return ResponseEntity.ok().build();
    }

    private String getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}