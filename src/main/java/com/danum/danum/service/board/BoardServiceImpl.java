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

    private final BoardRepository questionRepository;

    private final BoardMapper questionMapper;

    @Override
    @Transactional
    public void created(BoardNewDto boardNewDto) {
        Board question = questionMapper.toEntity(boardNewDto);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void resolved(Long id) {
        Board question =  validateNullableId(id);
        question.checkState();
    }

    @Override
    @Transactional
    public List<Board> boardViewList(Category category) {
        return questionRepository.findAllByCategory(category);
    }

    @Override
    @Transactional
    public Board boardView(Long id){
        return validateNullableId(id);
    }

    @Override
    @Transactional
    public Long updateBoard(BoardUpdateDto boardUpdateDto) {
        Board question = validateNullableId(boardUpdateDto.getId());
        switch (boardUpdateDto.getType()) {
            case LIKE :
                question.addLike();
                break;
            case COUNT:
                question.addCount();
                break;
        }
        questionRepository.save(question);

        return question.getLike();
    }

    @Override
    @Transactional
    public List<Board> boardSearchList(String keyword) {
        return questionRepository.findAllByTitleContaining(keyword);
    }

    private Board validateNullableId(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.NULL_BOARD_EXCEPTION));
    }

}
