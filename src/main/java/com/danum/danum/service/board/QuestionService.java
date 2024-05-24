package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearchDto;
import com.danum.danum.domain.board.QuestionViewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionService {

    public void created(QuestionNewDto newQuestionDto);

    public void resolved(Long id);

    public List<Question> boardViewList(QuestionViewDto questionView);

    public Question boardView(Long id);

    public Long incrementLikeCount(Long id);

    public Long incrementViewCount(Long id);

    public List<Question> boardSearchList(QuestionSearchDto questionSearch);

}
