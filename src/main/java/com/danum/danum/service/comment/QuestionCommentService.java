package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.domain.comment.question.QuestionCommentViewDto;

import java.util.List;

public interface QuestionCommentService {

    void create(QuestionCommentNewDto questionCommentNewDto);

    List<QuestionCommentViewDto> viewList(Long id);

    void update(QuestionCommentUpdateDto questionCommentUpdateDto, String loginUser);

    void delete(Long id, String loginUser);

}
