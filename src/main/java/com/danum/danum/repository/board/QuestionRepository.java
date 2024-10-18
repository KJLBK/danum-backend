package com.danum.danum.repository.board;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByMember(Member member);

    @Query("SELECT q FROM Question q ORDER BY q.view_count DESC")
    List<Question> findPopularQuestions(Pageable pageable);
}
