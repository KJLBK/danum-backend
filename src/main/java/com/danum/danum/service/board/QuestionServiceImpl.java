package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionMapper;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearch;
import com.danum.danum.domain.board.QuestionView;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.QuestionException;
import com.danum.danum.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    @Override
    public void created(QuestionNewDto newQuestionDto) {
        Question question = questionMapper.toEntity(newQuestionDto);
        questionRepository.save(question);
    }

    @Override
    public void resolved(Long id) {
        Question question =  validateNullableId(id);
        question.checkState();
    }

    @Override
    public Page<Question> boardViewList(QuestionView questionView) {
        Pageable pageable = PageRequest.of(questionView.getPage(), 10, Sort.by("id").ascending());
        return questionRepository.findByCategory(questionView.getCategory(), pageable);
    }

    @Override
    public Question boardView(Long id){
        Question question = validateNullableId(id);
        return question;
    }

    @Override
    public Long incrementLikeCount(Long id) {
        Question question = validateNullableId(id);
        question.addLike();
        questionRepository.save(question);

        return question.getLike();
    }

    @Override
    public Long incrementViewCount(Long id) {
        Question question = validateNullableId(id);
        question.addCount();
        questionRepository.save(question);

        return question.getCount();
    }

    @Override
    public Page<Question> boardSearchList(QuestionSearch questionSearch) {
        Pageable pageable = PageRequest.of(questionSearch.getPage(), 10, Sort.by("id").ascending());
        return questionRepository.findByTitleContaining(questionSearch.getKeyword(), pageable);
    }

    private Question validateNullableId(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionException(ErrorCode.NULL_BOARD_EXCEPTION));
    }

}
