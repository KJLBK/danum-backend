package com.danum.danum;

import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.service.comment.QuestionCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class QuestionCommentTest {

    @Autowired
    QuestionCommentService questionCommentService;

    @Test
    public void 게시판댓글생성() {
        QuestionCommentNewDto questionCommentNewDto = new QuestionCommentNewDto(1L, "test@naver.com", "test");
        questionCommentService.created(questionCommentNewDto);
    }

    @Test
    public void 게시판댓글전체조회() {
        System.out.println(questionCommentService.viewList(1L));
    }

    @Test
    public void 게시판댓글업데이트() {
        QuestionCommentUpdateDto questionCommentUpdateDto = new QuestionCommentUpdateDto(1L, "업데이트");
        questionCommentService.update(questionCommentUpdateDto);
    }

    @Test
    public void 게시판댓글삭제() {
        questionCommentService.delete(1L);
    }

}