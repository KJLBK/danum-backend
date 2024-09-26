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
    // 질문 관련 메서드
    List<QuestionViewDto> getAllQuestionViews();
    QuestionViewDto getQuestionView(Long id);
    void updateQuestion(Long id, Question updatedQuestion);
    void deleteQuestion(Long id);

    // 마을 게시글 관련 메서드
    List<VillageViewDto> getAllVillageViews();
    VillageViewDto getVillageView(Long id);
    void updateVillage(Long id, Village updatedVillage);
    void deleteVillage(Long id);

    // 댓글 관련 메서드
    void deleteQuestionComment(Long questionId, Long commentId);
    void deleteVillageComment(Long villageId, Long commentId);

    // 회원 관련 메서드
    List<Member> getAllMembers();
    Member getMemberByEmail(String email);
    void activateMember(String email);
    void deactivateMember(String email);
    void deleteMember(String email);

    // 회원의 게시글 및 댓글 조회
    List<QuestionViewDto> getMemberQuestions(String email);
    List<VillageViewDto> getMemberVillages(String email);
    Map<String, List<?>> getMemberComments(String email);

    // 질문/마을 게시판에 작성된 글안에 있는 댓글 삭제
    List<QuestionComment> getQuestionComments(Long questionId);
    List<VillageComment> getVillageComments(Long villageId);
}