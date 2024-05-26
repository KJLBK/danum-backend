package com.danum.danum;

import com.danum.danum.domain.comment.CommentDeleteDto;
import com.danum.danum.domain.comment.CommentNewDto;
import com.danum.danum.domain.comment.CommentUpdateDto;
import com.danum.danum.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CommentTest {

    @Autowired
    CommentService commentService;

    @Test
    public void createdComment(){
        CommentNewDto commentNewDto = new CommentNewDto(1L, "test@naver.com", "테스트 입니다.");
        commentService.created(commentNewDto);
    }

    @Test
    public void commentView(){
        System.out.println(commentService.commentView(1L));
    }

    @Test
    public void commentUpdate(){
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(1L, 1L, "성공?");
        commentService.update(commentUpdateDto);
    }

    @Test
    public void commentDelete(){
        CommentDeleteDto commentDeleteDto = new CommentDeleteDto(1L, 1L);
        commentService.delete(commentDeleteDto);
    }

}
