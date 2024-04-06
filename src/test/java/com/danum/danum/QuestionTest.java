package com.danum.danum;

import com.danum.danum.domain.board.QuestionFindDto;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.service.board.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class QuestionTest {

    @Autowired
    QuestionService questionService;

    @Test
    public void 게시판생성(){

        QuestionNewDto questionNewDto = new QuestionNewDto("test@naver.com", "제목", "내용");

        questionService.created(questionNewDto);

    }

}
