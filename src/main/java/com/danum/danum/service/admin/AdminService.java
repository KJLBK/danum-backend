package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;

import java.util.List;

public interface AdminService {

    List<Member> getMembers();

    List<Question> findBoardsByMemberName();

    List<QuestionComment> findCommentByQuestionId();

    List<VillageComment> findCommentByVillageId();

    void updateAllData();

    void updateMemberStatus();

}
