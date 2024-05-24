package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionMapper;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearchDto;
import com.danum.danum.domain.board.QuestionViewDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.QuestionException;
import com.danum.danum.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public void created(QuestionNewDto newQuestionDto) {
        Question question = questionMapper.toEntity(newQuestionDto);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void resolved(Long id) {
        Question question =  validateNullableId(id);
        question.checkState();
    }

    @Override
    @Transactional
    public List<Question> boardViewList(QuestionViewDto questionView) {
        return questionRepository.findAllByCategory(questionView.getCategory());
    }

    @Override
    @Transactional
    public Question boardView(Long id){
        return validateNullableId(id);
    }

    @Override
    @Transactional
    public Long incrementLikeCount(Long id) {
        Question question = validateNullableId(id);
        question.addLike();
        questionRepository.save(question);

        return question.getLike();
    }

    @Override
    @Transactional
    public Long incrementViewCount(Long id) {
        Question question = validateNullableId(id);
        question.addCount();
        questionRepository.save(question);

        return question.getCount();
    }

    @Override
    @Transactional
    public List<Question> boardSearchList(QuestionSearchDto questionSearch) {
        return questionRepository.findAllByTitleContaining(questionSearch.getKeyword());
    }

    private Question validateNullableId(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionException(ErrorCode.NULL_BOARD_EXCEPTION));
    }

}
