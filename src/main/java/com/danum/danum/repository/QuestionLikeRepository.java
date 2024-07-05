package com.danum.danum.repository;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionLike;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {

    //@Query("SELECT q FROM QuestionLike q WHERE q.question = :id AND q.member = :email")
    //Optional<QuestionLike> findByQuestionAndMember(@Param("id")Question question, @Param("email")Member member);

}
