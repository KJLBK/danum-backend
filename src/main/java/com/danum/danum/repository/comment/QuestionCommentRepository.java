package com.danum.danum.repository.comment;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Long> {

    List<QuestionComment> findAllByQuestionId(Long questionId);

    List<QuestionComment> findAllByMember(Member member);

}
