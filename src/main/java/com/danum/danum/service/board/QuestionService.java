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

    public Long incrementLikeCount(Long id);

    public Long incrementViewCount(Long id);

}
