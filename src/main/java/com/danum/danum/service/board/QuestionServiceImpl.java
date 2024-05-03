package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionFindDto;
import com.danum.danum.domain.board.QuestionMapper;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
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

    @Override
    public void created(QuestionNewDto newQuestionDto) {
        Question question = QuestionMapper.toEntity(newQuestionDto);
        questionRepository.save(question);
    }

    @Override
    public void resolved(QuestionFindDto questionFindDto) {
        Optional<Question> check = questionRepository.findById(questionFindDto.getId());
        if(check.isEmpty()){
            throw new MemberException(ErrorCode.NULLBOARD_EXCEPTION);
        }

        Question question = check.get();
        question.checkState();
    }

    @Override
    public Page<Question> search(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());

        return questionRepository.findAll(pageable);
    }

    @Override
    public Question like(QuestionFindDto questionFindDto) {
        return null;
    }

}
