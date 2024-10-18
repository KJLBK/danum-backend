package com.danum.danum;

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
        villageService.create(villageNewDto);
    }

//    @Test
//    public void 질문게시판전체조회() {
//        System.out.println(villageService.viewList());
//    }

}
