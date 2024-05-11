package com.danum.danum.domain.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuestionSearch {

    private String keyword;

    private int page;

}
