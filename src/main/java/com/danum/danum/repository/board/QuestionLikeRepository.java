package com.danum.danum.repository.board;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionLike;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {

    Optional<QuestionLike> findByQuestionIdAndMemberEmail(Question question, Member member);

}
