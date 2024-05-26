package com.danum.danum.service.board;

import com.danum.danum.domain.board.Board;
import com.danum.danum.domain.board.BoardNewDto;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.Category;

import java.util.List;

public interface BoardService {

    void created(BoardNewDto boardNewDtoDto);

    void resolved(Long id);

    List<Board> boardViewList(Category category);

    Board boardView(Long id);

    Long updateBoard(BoardUpdateDto boardUpdateDto);

    List<Board> boardSearchList(String keyword);

}
