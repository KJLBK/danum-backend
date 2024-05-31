package com.danum.danum.repository;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, QuestionCommentId> {

    List<QuestionComment> findAllByQuestionId(Long questionId);

    Optional<QuestionComment> findByQuestionCommentId_CommentId(Long commentId);

}
