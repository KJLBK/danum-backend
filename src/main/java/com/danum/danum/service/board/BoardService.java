package com.danum.danum.service.board;

import com.danum.danum.domain.board.Board;
import com.danum.danum.domain.board.BoardNewDto;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.Category;

import java.util.List;

public interface BoardService {

    void created(BoardNewDto boardNewDtoDto);

    List<Board> boardViewList(int category);

    Board boardView(Long id);

    void updateBoard(BoardUpdateDto boardUpdateDto);

    List<Board> boardSearchList(String keyword);

}
