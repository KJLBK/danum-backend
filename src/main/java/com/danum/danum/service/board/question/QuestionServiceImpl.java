package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionMapper;
import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;


    @Override
    @Transactional
    public void created(QuestionNewDto questionNewDto) {
        if (questionNewDto.getEmail().isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }
        Question question = questionMapper.toEntity(questionNewDto);

        questionRepository.save(question);
    }

    @Override
    @Transactional
    public List<QuestionViewDto> viewList() {
        List<Question> questionList = questionRepository.findAll();
        List<QuestionViewDto> questionViewDtoList = new ArrayList<>();
        for (Question question : questionList) {
            questionViewDtoList.add(new QuestionViewDto().toEntity(question));
        }

        return questionViewDtoList;
    }

    @Override
    @Transactional
    public QuestionViewDto view(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        return new QuestionViewDto().toEntity(question);
    }

}
