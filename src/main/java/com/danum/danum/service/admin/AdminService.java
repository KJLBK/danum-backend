package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 회원 관련 메서드
    List<Member> getAllMembers();
    Member getMemberByEmail(String email);

    // 회원의 게시글 및 댓글 조회
    List<QuestionViewDto> getMemberQuestions(String email);
    List<VillageViewDto> getMemberVillages(String email);
    Map<String, List<?>> getMemberComments(String email);

    void deleteQuestionComment(Long questionId, Long commentId);
    void deleteVillageComment(Long villageId, Long commentId);

}