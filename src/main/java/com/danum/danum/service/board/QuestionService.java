package com.danum.danum.service.board;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionNewDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {

    public void created(QuestionNewDto newQuestionDto);

    public void resolved(Long id);

    public Page<Question> search(int page);

    public Question oneSearch(Long id);

    public Question incrementLikeCount(Long id);

    public Question incrementViewCount(Long id);

}
