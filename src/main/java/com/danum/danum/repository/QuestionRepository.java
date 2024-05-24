package com.danum.danum.repository;

import com.danum.danum.domain.board.Category;
import com.danum.danum.domain.board.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question>  findAllByTitleContaining(String searchKeyword);

    List<Question> findAllByCategory(Category category);

}
