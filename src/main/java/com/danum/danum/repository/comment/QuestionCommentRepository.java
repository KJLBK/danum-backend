package com.danum.danum.repository.comment;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Long> {

    List<QuestionComment> findAllByQuestionId(Long questionId);

    List<QuestionComment> findAllByMember(Member member);

    List<QuestionComment> findAllByQuestion(Question question);

    Optional<QuestionComment> findByQuestionAndIsAcceptedTrue(Question question);

    Optional<QuestionComment> findByQuestionIdAndIsAcceptedTrue(Long questionId);

    boolean existsByQuestionAndIsAcceptedTrue(Question question);

    long countByQuestionAndIsAcceptedTrue(Question question);
}
