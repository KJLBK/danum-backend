package com.danum.danum.service.board;

import com.danum.danum.domain.board.Category;
import com.danum.danum.domain.board.Board;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.BoardNewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BoardService {

    public void created(BoardNewDto boardNewDtoDto);

    public void resolved(Long id);

    public List<Board> boardViewList(Category category);

    public Board boardView(Long id);

    public Long updateBoard(BoardUpdateDto boardUpdateDto);

    public List<Board> boardSearchList(String keyword);

}
