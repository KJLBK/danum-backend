package com.danum.danum.controller.board;

import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.service.comment.VillageCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<?> created(@RequestBody VillageCommentNewDto villageCommentNewDto) {
        villageCommentService.created(villageCommentNewDto);

        return ResponseEntity.ok("댓글 생성 성공");
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> viewList(@PathVariable("id") Long id) {
        return ResponseEntity.ok(villageCommentService.viewList(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody VillageCommentUpdateDto villageCommentUpdateDto) {
        villageCommentService.update(villageCommentUpdateDto);

        return ResponseEntity.ok("댓글 수정 성공");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        villageCommentService.delete(id);

        return ResponseEntity.ok("댓글 삭제 성공");
    }

}
