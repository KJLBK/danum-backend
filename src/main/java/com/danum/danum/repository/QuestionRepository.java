package com.danum.danum.repository;

import com.danum.danum.domain.board.Question;
import com.danum.danum.domain.board.QuestionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<QuestionList> findAllBy();

}
