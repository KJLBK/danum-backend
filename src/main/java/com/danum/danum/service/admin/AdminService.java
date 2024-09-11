package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;

import java.util.List;

public interface AdminService {

    List<Member> viewMembers();

    List<Question> viewQuestionBoards();

    List<Village> viewVillageBoards();

}
