package com.danum.danum.controller;

import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.service.board.question.QuestionService;
import com.danum.danum.service.board.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainPageController {

    private final QuestionService questionService;
    private final VillageService villageService;

    private Map<String, List<?>> cachedPopularPosts = new HashMap<>();

    @GetMapping("/recent-posts")
    public ResponseEntity<Map<String, List<?>>> getRecentPosts() {
        PageRequest pageRequest = PageRequest.of(0, 5); // 첫 페이지, 5개 항목

        List<QuestionViewDto> recentQuestions = questionService.viewList(pageRequest).getContent();
        List<VillageViewDto> recentVillages = villageService.viewList(pageRequest).getContent();

        Map<String, List<?>> response = new HashMap<>();
        response.put("recentQuestions", recentQuestions);
        response.put("recentVillages", recentVillages);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular-posts")
    public ResponseEntity<Map<String, List<?>>> getPopularPosts() {
        return ResponseEntity.ok(cachedPopularPosts);
    }

    @Scheduled(fixedRate = 300000) // 5분(300,000ms)마다 실행
    @CacheEvict(value = "popularPosts", allEntries = true)
    public void updatePopularPosts() {
        int limit = 5;

        List<QuestionViewDto> popularQuestions = questionService.getPopularQuestions(limit);
        List<VillageViewDto> popularVillages = villageService.getPopularVillages(limit);

        cachedPopularPosts = new HashMap<>();
        cachedPopularPosts.put("popularQuestions", popularQuestions);
        cachedPopularPosts.put("popularVillages", popularVillages);
    }
}
