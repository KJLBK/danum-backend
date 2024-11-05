package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionUpdateDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionService {

    void create(QuestionNewDto questionNewDto);

    Page<QuestionViewDto> viewList(Pageable pageable);

    QuestionViewDto view(Long id, String email);

    void likeStatus(Long id, String email);

    void deleteQuestion(Long id);

    void update(QuestionUpdateDto questionUpdateDto, String loginUser);

    List<QuestionViewDto> getPopularQuestions(int limit);

    boolean hasAcceptedComment(Long questionId);

    Page<QuestionViewDto> getQuestionsByRegion(String city, String district, Pageable pageable);

    Page<QuestionViewDto> searchQuestions(String keyword, Pageable pageable);
}
