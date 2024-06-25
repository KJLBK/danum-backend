package com.danum.danum;

import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.service.comment.VillageCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class VillageCommentTest {

    @Autowired
    VillageCommentService villageCommentService;

    @Test
    public void 게시판댓글생성() {
        VillageCommentNewDto villageCommentNewDto = new VillageCommentNewDto(1L, "test@naver.com", "test");
        villageCommentService.create(villageCommentNewDto);
    }

    @Test
    public void 게시판댓글전체조회() {
        System.out.println(villageCommentService.viewList(1L));
    }

    @Test
    public void 게시판댓글업데이트() {
        VillageCommentUpdateDto villageCommentUpdateDto = new VillageCommentUpdateDto(1L, "업데이트");
        villageCommentService.update(villageCommentUpdateDto);
    }

    @Test
    public void 게시판댓글삭제() {
        villageCommentService.delete(1L);
    }

}
