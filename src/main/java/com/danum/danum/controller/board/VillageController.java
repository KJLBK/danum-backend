package com.danum.danum.controller.board;

import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillageUpdateDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.service.board.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 특정 거리 내의 Village 게시글을 조회
     * @param latitude 사용자의 위도
     * @param longitude 사용자의 경도
     * @param distance 검색할 반경 거리 (km)
     * @return 지정된 거리 내의 Village 게시글 목록
     */
    @GetMapping("/by-distance")
    public ResponseEntity<List<VillageViewDto>> getVillagesByDistance(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double distance) {
        List<VillageViewDto> villages = villageService.getVillagesByDistance(latitude, longitude, distance);
        return ResponseEntity.ok(villages);
    }

    /**
     * 카테고리별로 Village 게시글을 조회
     * @param latitude 사용자의 위도
     * @param longitude 사용자의 경도
     * @param category 검색할 카테고리 (가까운동네, 중간 거리 동네, 먼 동네)
     * @return 해당 카테고리에 속하는 Village 게시글 목록
     */
    @GetMapping("/by-category")
    public ResponseEntity<List<VillageViewDto>> getVillagesByCategory(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam String category) {
        List<VillageViewDto> villages = villageService.getVillagesByCategory(latitude, longitude, category);
        return ResponseEntity.ok(villages);
    }
}
