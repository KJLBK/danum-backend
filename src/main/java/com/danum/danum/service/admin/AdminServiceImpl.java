package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Override
    public List<Member> getMembers() {
        return null;
    }

    @Override
    public List<Question> findBoardsByMemberName() {
        return null;
    }

    @Override
    public List<QuestionComment> findCommentByQuestionId() {
        return null;
    }

    @Override
    public List<VillageComment> findCommentByVillageId() {
        return null;
    }

    @Override
    public void updateAllData() {

    }

    @Override
    public void updateMemberStatus() {

    }

}
