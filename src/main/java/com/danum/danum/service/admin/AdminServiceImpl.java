package com.danum.danum.service.admin;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.QuestionRepository;
import com.danum.danum.repository.board.VillageRepository;
import com.danum.danum.repository.comment.QuestionCommentRepository;
import com.danum.danum.repository.comment.VillageCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<QuestionViewDto> getAllQuestionViews() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream().map(QuestionViewDto::from).collect(Collectors.toList());
    }

    @Override
    public QuestionViewDto getQuestionView(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        return QuestionViewDto.from(question);
    }

    @Override
    public void updateQuestion(Long id, Question updatedQuestion) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        // Update fields of the question
        questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public List<VillageViewDto> getAllVillageViews() {
        List<Village> villages = villageRepository.findAll();
        return villages.stream().map(village -> new VillageViewDto().toEntity(village)).collect(Collectors.toList());
    }

    @Override
    public VillageViewDto getVillageView(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        return new VillageViewDto().toEntity(village);
    }

    @Override
    public void updateVillage(Long id, Village updatedVillage) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        // Update fields of the village
        villageRepository.save(village);
    }

    @Override
    public void deleteVillage(Long id) {
        villageRepository.deleteById(id);
    }

    @Override
    public void deleteQuestionComment(Long commentId) {
        questionCommentRepository.deleteById(commentId);
    }

    @Override
    public void deleteVillageComment(Long commentId) {
        villageCommentRepository.deleteById(commentId);
    }

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
    public void activateMember(String email) {
        Member member = getMemberByEmail(email);
        member.setContribution(0);
        memberRepository.save(member);
    }

    @Override
    public void deactivateMember(String email) {
        Member member = getMemberByEmail(email);
        member.setContribution(1);
        memberRepository.save(member);
    }

    @Override
    public void deleteMember(String email) {
        memberRepository.deleteById(email);
    }

    @Override
    public List<QuestionViewDto> getMemberQuestions(String email) {
        Member member = getMemberByEmail(email);
        List<Question> questions = questionRepository.findAllByMember(member);
        return questions.stream().map(QuestionViewDto::from).collect(Collectors.toList());
    }

    @Override
    public List<VillageViewDto> getMemberVillages(String email) {
        Member member = getMemberByEmail(email);
        List<Village> villages = villageRepository.findAllByMember(member);
        return villages.stream().map(village -> new VillageViewDto().toEntity(village)).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<?>> getMemberComments(String email) {
        Member member = getMemberByEmail(email);
        Map<String, List<?>> comments = new HashMap<>();
        comments.put("questionComments", questionCommentRepository.findAllByMember(member));
        comments.put("villageComments", villageCommentRepository.findAllByMember(member));
        return comments;
    }
}