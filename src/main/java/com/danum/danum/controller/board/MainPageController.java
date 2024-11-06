package com.danum.danum.controller.board;

import com.danum.danum.domain.board.page.PagedResponseDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.notification.NotificationDto;
import com.danum.danum.repository.PostDateComparable;
import com.danum.danum.service.board.question.QuestionService;
import com.danum.danum.service.board.village.VillageService;
import com.danum.danum.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainPageController {

    private final QuestionService questionService;
    private final VillageService villageService;
    private final NotificationService notificationService;

    private Map<String, List<?>> cachedPopularPosts = new HashMap<>();

    @GetMapping("/recent-posts")
    public ResponseEntity<PagedResponseDto<Object>> getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<QuestionViewDto> questionPage = questionService.viewList(pageRequest);
        Page<VillageViewDto> villagePage = villageService.viewList(pageRequest);

        List<QuestionViewDto> allQuestions = questionService.viewList(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        List<VillageViewDto> allVillages = villageService.viewList(PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        List<Object> allPosts = Stream.concat(
                allQuestions.stream(),
                allVillages.stream()
        ).sorted(Comparator.comparing(
                post -> ((PostDateComparable) post).getCreated_at()
        ).reversed()).collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, allPosts.size());
        List<Object> pagedPosts = allPosts.subList(start, end);

        long totalElements = allPosts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(PagedResponseDto.builder()
                .content(pagedPosts)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(page >= totalPages - 1)
                .build());
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

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<NotificationDto> notifications = notificationService.getNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(Authentication authentication) {
        String userEmail = authentication.getName();
        long unreadCount = notificationService.getUnreadCount(userEmail);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<Object>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<QuestionViewDto> allQuestions = questionService.searchQuestions(keyword, PageRequest.of(0, Integer.MAX_VALUE));
        Page<VillageViewDto> allVillages = villageService.searchVillages(keyword, PageRequest.of(0, Integer.MAX_VALUE));

        List<Object> allPosts = Stream.concat(
                        allQuestions.getContent().stream(),
                        allVillages.getContent().stream()
                )
                .sorted(Comparator.comparing(
                        post -> ((PostDateComparable) post).getCreated_at()
                ).reversed())
                .collect(Collectors.toList());

        long totalElements = allPosts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page >= totalPages && totalElements > 0) {
            page = totalPages - 1;
        }

        int start = page * size;
        int end = Math.min(start + size, allPosts.size());
        List<Object> pagedContent = allPosts.subList(start, end);

        return ResponseEntity.ok(PagedResponseDto.builder()
                .content(pagedContent)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(page >= totalPages - 1)
                .build());
    }
}
