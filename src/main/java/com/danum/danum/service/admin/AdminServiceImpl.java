package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Override
    public List<Member> viewMembers() {
        return null;
    }

    @Override
    public List<Question> viewQuestionBoards() {
        return null;
    }

    @Override
    public List<Village> viewVillageBoards() {
        return null;
    }

}
