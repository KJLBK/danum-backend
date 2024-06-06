package com.danum.danum.repository;

import com.danum.danum.domain.comment.question.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Long> {

    List<QuestionComment> findAllByQuestionId(Long questionId);

}
