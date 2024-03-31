package com.danum.danum.service.board;

import com.danum.danum.domain.board.QuestionFindDto;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.Question;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {

    public Question created(QuestionNewDto newQuestionDto);

    public void stop(QuestionFindDto questionFindDto);

    public void start(QuestionFindDto questionFindDto);

}
