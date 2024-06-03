package com.danum.danum.repository;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.view.QuestionView;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionViewRepository extends JpaRepository<QuestionView, Long> {

    Optional<QuestionView> findByQuestionAndMember(Question question, Member member);

}
