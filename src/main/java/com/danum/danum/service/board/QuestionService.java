package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionFindDto;
import com.danum.danum.domain.board.QuestionNewDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {

    public void created(QuestionNewDto newQuestionDto);

    public void resolved(QuestionFindDto questionFindDto);

    public Page<Question> search(int page);

    public Question like(QuestionFindDto questionFindDto);

}
