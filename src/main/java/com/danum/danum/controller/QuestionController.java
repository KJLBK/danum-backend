package com.danum.danum.controller;

import com.danum.danum.service.board.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

}
