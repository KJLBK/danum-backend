package com.danum.danum.service.board;

import com.danum.danum.domain.board.QuestionFindDto;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionService {

    public Question created(QuestionNewDto newQuestionDto);

    public void reSolved(QuestionFindDto questionFindDto);

    public List<Question> search();

    public Question like(QuestionFindDto questionFindDto);

}
