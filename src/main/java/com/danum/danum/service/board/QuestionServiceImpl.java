package com.danum.danum.service.board;

import com.danum.danum.domain.board.*;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Question created(QuestionNewDto newQuestionDto) {

        Question question = QuestionMapper.toEntity(newQuestionDto);

        return questionRepository.save(question);

    }

    @Override
    public void stop(QuestionFindDto questionFindDto) {
        Optional<Question> check = questionRepository.findById(String.valueOf(questionFindDto.getId()));

        if(check.isEmpty()){
            throw new MemberException(ErrorCode.NULLBOARD_EXCEPTION);
        }

        Question question = check.get();
        question.checkState(false);
    }

    @Override
    public void start(QuestionFindDto questionFindDto) {
        Optional<Question> check = questionRepository.findById(String.valueOf(questionFindDto.getId()));

        if(check.isEmpty()){
            throw new MemberException(ErrorCode.NULLBOARD_EXCEPTION);
        }

        Question question = check.get();
        question.checkState(true);
    }

    @Override
    public List<QuestionList> seauch() {
        return questionRepository.findAllBy();
    }

}
