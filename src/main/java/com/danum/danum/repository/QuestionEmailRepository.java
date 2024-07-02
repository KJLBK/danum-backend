package com.danum.danum.repository;

import com.danum.danum.domain.board.question.QuestionEmailToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionEmailRepository extends CrudRepository<QuestionEmailToken, Long> {
}
