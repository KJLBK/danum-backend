package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.QuestionRepository;
import com.danum.danum.repository.board.VillageRepository;
import com.danum.danum.repository.comment.QuestionCommentRepository;
import com.danum.danum.repository.comment.VillageCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private VillageRepository villageRepository;
    @Autowired
    private QuestionCommentRepository questionCommentRepository;
    @Autowired
    private VillageCommentRepository villageCommentRepository;

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionViewDto> getMemberQuestions(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        return questionRepository.findAllByMember(member).stream()
                .map(QuestionViewDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getMemberVillages(String email, Pageable pageable) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        return villageRepository.findAllByMember(member, pageable)
                .map(village -> new VillageViewDto().toEntity(village));
    }

    @Override
    public Map<String, List<?>> getMemberComments(String email) {
        Member member = getMemberByEmail(email);
        Map<String, List<?>> comments = new HashMap<>();
        comments.put("questionComments", questionCommentRepository.findAllByMember(member));
        comments.put("villageComments", villageCommentRepository.findAllByMember(member));
        return comments;
    }

    @Override
    public void deleteQuestionComment(Long questionId, Long commentId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        if (!comment.getQuestion().getId().equals(questionId)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
        }

        questionCommentRepository.delete(comment);
    }

    @Override
    public void deleteVillageComment(Long villageId, Long commentId) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        VillageComment comment = villageCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        if (!comment.getVillage().getId().equals(villageId)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
        }

        villageCommentRepository.delete(comment);
    }
}