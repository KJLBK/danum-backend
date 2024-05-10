package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionMapper;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.QuestionException;
import com.danum.danum.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public Page<Question> search(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());
        return questionRepository.findAll(pageable);
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

    private Question validateNullableId(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionException(ErrorCode.NULL_BOARD_EXCEPTION));
    }

}
