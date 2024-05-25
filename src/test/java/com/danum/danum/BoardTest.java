package com.danum.danum;

import com.danum.danum.domain.board.BoardNewDto;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.Category;
import com.danum.danum.domain.board.Type;
import com.danum.danum.service.board.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class BoardTest {

    @Autowired
    private BoardService boardService;

    @Test
    public void 게시판생성(){
        BoardNewDto newbie = new BoardNewDto("test1@naver.com", "test", "test", Category.VILLAGE);
        boardService.created(newbie);
    }

    @Test
    public void 게시판상태변경(){
        BoardUpdateDto update = new BoardUpdateDto(1L, Type.COUNT);
        boardService.updateBoard(update);
    }


}
