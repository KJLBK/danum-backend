package com.danum.danum.controller.board;

import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.service.board.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/village")
public class VillageController {

    private final VillageService villageService;

    @PostMapping("/new")
    public ResponseEntity<?> created(@RequestBody VillageNewDto villageNewDto){
        villageService.created(villageNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/show")
    public ResponseEntity<?> viewList(){
        return ResponseEntity.ok(villageService.viewList());
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> view(@PathVariable("id") Long id){
        return ResponseEntity.ok(villageService.view(id));
    }

    @PutMapping("/like/{id}")
    public ResponseEntity<?> like(@PathVariable("id") Long id) {
        boolean check = villageService.updateLike(id);
        if (check) {
            return ResponseEntity.ok("좋아요 성공");
        }

        return ResponseEntity.ok("좋아요 취소");
    }

}