package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;

import java.util.List;

public interface QuestionCommentService {

    void created(QuestionCommentNewDto questionCommentNewDto);

    List<QuestionComment> viewList(Long id);

    void update(QuestionCommentUpdateDto questionCommentUpdateDto);

    void delete(Long id);

}
