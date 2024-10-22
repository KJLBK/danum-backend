package com.danum.danum.controller.board;

import com.danum.danum.domain.board.page.PagedResponseDto;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillagePostType;
import com.danum.danum.domain.board.village.VillageUpdateDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.board.page.PagedResponseDto;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.board.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/village")
public class VillageController {

    private final VillageService villageService;
    private final AdminService adminService;

    @PostMapping("/new")
    public ResponseEntity<?> createVillageBoard(@RequestBody VillageNewDto villageNewDto){
        villageService.create(villageNewDto);
        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/show")
    public ResponseEntity<PagedResponseDto<VillageViewDto>> getVillageBoardList(@PageableDefault(size = 10) Pageable pageable){
        Page<VillageViewDto> villagePage = villageService.viewList(pageable);
        return ResponseEntity.ok(PagedResponseDto.from(villagePage));
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<?> getVillageBoardById(@PathVariable("id") Long id){
        return ResponseEntity.ok(villageService.view(id, getLoginUser()));
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeStatus(@PathVariable("id") Long id) {
        villageService.likeStatus(id, getLoginUser());
        return ResponseEntity.ok("좋아요 관련 성공");
    }

    @DeleteMapping("/villages/{id}")
    public ResponseEntity<?> deleteVillage(@PathVariable Long id) {
        villageService.deleteVillage(id);
        return ResponseEntity.ok("마을 게시글이 삭제되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateVillageBoard(@RequestBody VillageUpdateDto villageUpdateDto) {
        villageService.update(villageUpdateDto, getLoginUser());
        return ResponseEntity.ok("게시판 수정 성공");
    }

    private String getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/by-type/{postType}")
    public ResponseEntity<PagedResponseDto<VillageViewDto>> getVillagesByPostType(
            @PathVariable VillagePostType postType,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<VillageViewDto> villagePage = villageService.getVillagesByPostType(postType, pageable);
        return ResponseEntity.ok(PagedResponseDto.from(villagePage));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members/{email}/villages")
    public ResponseEntity<PagedResponseDto<VillageViewDto>> getMemberVillages(
            @PathVariable("email") String email,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<VillageViewDto> villages = adminService.getMemberVillages(email, pageable);
        return ResponseEntity.ok(PagedResponseDto.from(villages));
    }

    @GetMapping("/{id}/has-accepted-comment")
    public ResponseEntity<Boolean> hasAcceptedComment(@PathVariable Long id) {
        return ResponseEntity.ok(villageService.hasAcceptedComment(id));
    }

    @GetMapping("/local")
    public ResponseEntity<PagedResponseDto<VillageViewDto>> getLocalVillages(
            @PageableDefault(size = 10) Pageable pageable) {
        String userEmail = getLoginUser();
        Page<VillageViewDto> villages = villageService.getLocalVillages(userEmail, pageable);
        return ResponseEntity.ok(PagedResponseDto.from(villages));
    }
}