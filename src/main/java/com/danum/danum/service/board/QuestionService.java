package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionNewDto;
import com.danum.danum.domain.board.QuestionSearch;
import com.danum.danum.domain.board.QuestionView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {

    public void created(QuestionNewDto newQuestionDto);

    public void resolved(Long id);

    public Page<Question> boardViewList(QuestionView questionView);

    public Question boardView(Long id);

    public Long incrementLikeCount(Long id);

    public Long incrementViewCount(Long id);

    public Page<Question> boardSearchList(QuestionSearch questionSearch);

}
