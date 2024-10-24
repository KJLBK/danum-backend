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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;

    private final QuestionRepository questionRepository;

    private final VillageRepository villageRepository;

    private final QuestionCommentRepository questionCommentRepository;

    private final VillageCommentRepository villageCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> getMemberQuestions(String email, Pageable pageable) {
        Member member = getMemberByEmail(email);
        return questionRepository.findPageByMember(member, pageable)
                .map(QuestionViewDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getMemberVillages(String email, Pageable pageable) {
        Member member = getMemberByEmail(email);
        return villageRepository.findAllByMember(member, pageable)
                .map(village -> new VillageViewDto().toEntity(village));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<?>> getMemberComments(String email) {
        Member member = getMemberByEmail(email);
        Map<String, List<?>> comments = new HashMap<>();
        comments.put("questionComments", questionCommentRepository.findAllByMember(member));
        comments.put("villageComments", villageCommentRepository.findAllByMember(member));
        return comments;
    }

    @Override
    @Transactional
    public void deleteQuestionComment(Long questionId, Long commentId) {
        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        validateCommentBelongsToPost(questionId, comment.getQuestion().getId());
        questionCommentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteVillageComment(Long villageId, Long commentId) {
        VillageComment comment = villageCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        validateCommentBelongsToPost(villageId, comment.getVillage().getId());
        villageCommentRepository.delete(comment);
    }

    private void validateCommentBelongsToPost(Long postId, Long commentPostId) {
        boolean commentBelongsToPost = postId.equals(commentPostId);
        switch(String.valueOf(!commentBelongsToPost)) {
            case "true":
                throw new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
        }
    }
}