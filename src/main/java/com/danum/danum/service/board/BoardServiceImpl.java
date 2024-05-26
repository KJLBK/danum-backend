package com.danum.danum.service.board;

import com.danum.danum.domain.board.Board;
import com.danum.danum.domain.board.BoardMapper;
import com.danum.danum.domain.board.BoardNewDto;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.Category;
import com.danum.danum.exception.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

    @Override
    @Transactional
    public void created(BoardNewDto boardNewDto) {
        if (boardNewDto.getCategory() == null) {
            throw new BoardException(ErrorCode.NOT_CATEGORY_EXCEPTION);
        }
        Board question = boardMapper.toEntity(boardNewDto);
        boardRepository.save(question);
    }

    @Override
    @Transactional
    public List<Board> boardViewList(int category) {
        if (category == 1) {
            return boardRepository.findAllByCategory(Category.VILLAGE);
        }

        return boardRepository.findAllByCategory(Category.QUESTION);
    }

    @Override
    @Transactional
    public Board boardView(Long id){
        return validateNullableId(id);
    }

    @Override
    @Transactional
    public void updateBoard(BoardUpdateDto boardUpdateDto) {
        Board board = validateNullableId(boardUpdateDto.getId());
        switch (boardUpdateDto.getType()) {
            case LIKE :
                board.addLike();
                break;
            case COUNT:
                board.addCount();
                break;
            case STOP:
                board.checkState();
                break;
        }
        boardRepository.save(board);
    }

    @Override
    @Transactional
    public List<Board> boardSearchList(String keyword) {
        return boardRepository.findAllByTitleContaining(keyword);
    }

    private Board validateNullableId(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.NULL_BOARD_EXCEPTION));
    }

}
