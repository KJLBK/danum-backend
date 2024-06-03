package com.danum.danum.domain.board.village.view;

import com.danum.danum.domain.board.question.view.QuestionView;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VillageViewMapper {

    public VillageView toEntity(Village village, Member member) {
        return VillageView.builder()
                .village(village)
                .member(member)
                .viewed_at(LocalDateTime.now())
                .liked(false).build();
    }

}
