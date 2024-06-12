package com.danum.danum;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.service.board.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuestionTest {

    @Autowired
    private QuestionService questionService;

    @Test
    public void 질문게시판생성() {
        QuestionNewDto questionNewDto = new QuestionNewDto("test@naver.com","test", "test", null);
        questionService.created(questionNewDto);
    }

    @Test
    public void 질문게시판전체조회() {
        System.out.println(questionService.viewList());
    }

}
