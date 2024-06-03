package com.danum.danum;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.service.board.village.VillageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class VillageTest {

    @Autowired
    private VillageService villageService;

    @Test
    public void 질문게시판생성() {
        VillageNewDto villageNewDto = new VillageNewDto("test@naver.com","test", "test");
        villageService.created(villageNewDto);
    }

    @Test
    public void 질문게시판전체조회() {
        System.out.println(villageService.viewList());
    }

    @Test
    public void 질문게시판조회및조회수() {
        System.out.println(villageService.view(1L));
    }

    @Test
    public void 질문게시판좋아요() {
        System.out.println(villageService.view(1L));
        System.out.println(villageService.updateLike(1L));
    }

}
