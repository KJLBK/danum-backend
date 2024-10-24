package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 회원 관련 메서드
    List<Member> getAllMembers();
    Member getMemberByEmail(String email);

    // 회원의 게시글 및 댓글 조회
    Page<QuestionViewDto> getMemberQuestions(String email, Pageable pageable);
    Page<VillageViewDto> getMemberVillages(String email, Pageable pageable);
    Map<String, List<?>> getMemberComments(String email);

    // 댓글 삭제
    void deleteQuestionComment(Long questionId, Long commentId);
    void deleteVillageComment(Long villageId, Long commentId);
}