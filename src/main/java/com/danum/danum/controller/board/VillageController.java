package com.danum.danum.controller.board;

import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.service.board.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/village")
public class VillageController {

    private final VillageService villageService;

    @PostMapping("/new")
    public ResponseEntity<?> createVillageBoard(@RequestBody VillageNewDto villageNewDto){
        villageService.create(villageNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/show")
    public ResponseEntity<?> getVillageBoardList(){
        return ResponseEntity.ok(villageService.viewList());
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getVillageBoardById(@PathVariable("id") Long id){
        return ResponseEntity.ok(villageService.view(id, getLoginUser()));
    }

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

}
