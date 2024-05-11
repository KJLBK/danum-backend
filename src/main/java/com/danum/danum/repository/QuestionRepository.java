package com.danum.danum.repository;

import com.danum.danum.domain.board.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByTitleContaining(String searchKeyword, Pageable pageable);

}
