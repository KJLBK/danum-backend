package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class QuestionCommentMapper {

    private final QuestionRepository questionRepository;

    private final MemberRepository memberRepository;

    public QuestionComment toEntity(QuestionCommentNewDto questionCommentNewDto) {
        Member member = memberRepository.findById(questionCommentNewDto.getMember_email())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        Question question = questionRepository.findById(questionCommentNewDto.getQuestion_id())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        return QuestionComment.builder()
                .member(member)
                .question(question)
                .content(questionCommentNewDto.getContent())
                .created_at(LocalDateTime.now())
                .build();
    }

}
