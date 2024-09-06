package com.danum.danum.controller.comment;

import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.service.comment.VillageCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/village/comment")
public class VillageCommentController {

    private final VillageCommentService villageCommentService;

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

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

}
