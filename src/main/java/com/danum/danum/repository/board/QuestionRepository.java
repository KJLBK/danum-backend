package com.danum.danum.repository.board;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByMember(Member member);
    Page<Question> findPageByMember(Member member, Pageable pageable);

    @Query("SELECT q FROM Question q ORDER BY q.view_count DESC")
    List<Question> findPopularQuestions(Pageable pageable);

    Page<Question> findByAddressTagStartingWith(String addressTag, Pageable pageable);

    @Query("SELECT q FROM Question q ORDER BY q.created_at DESC")
    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% ORDER BY q.created_at DESC")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
