package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionViewDto;

import java.util.List;

public interface QuestionService {

    void create(QuestionNewDto questionNewDto);

    List<QuestionViewDto> viewList();

    QuestionViewDto view(Long id);

}
