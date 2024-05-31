package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionNewDto;

import java.util.List;

public interface QuestionService {

    void created(QuestionNewDto questionNewDto);

    List<Question> viewList();

    Question view(Long id);

    boolean updateLike(Long id);

}
