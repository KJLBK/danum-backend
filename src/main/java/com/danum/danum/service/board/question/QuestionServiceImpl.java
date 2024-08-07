package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionEmailToken;
import com.danum.danum.domain.board.question.QuestionLike;
import com.danum.danum.domain.board.question.QuestionMapper;
import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.QuestionViewDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.QuestionEmailRepository;
import com.danum.danum.repository.board.QuestionLikeRepository;
import com.danum.danum.repository.board.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    private final QuestionEmailRepository questionEmailRepository;

    private final MemberRepository memberRepository;

    private final QuestionLikeRepository questionLikeRepository;

    @Override
    @Transactional
    public void create(QuestionNewDto questionNewDto) {
        memberRepository.findById(questionNewDto.getEmail())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        Question question = questionMapper.toEntity(questionNewDto);

        questionRepository.save(question);
    }

    @Override
    @Transactional
    public List<QuestionViewDto> viewList() {
        List<Question> questionList = questionRepository.findAll();
        List<QuestionViewDto> questionViewDtoList = new ArrayList<>();
        for (Question question : questionList) {
            questionViewDtoList.add(QuestionViewDto.from(question));
        }

        return questionViewDtoList;
    }

    @Override
    @Transactional
    public QuestionViewDto view(Long id, String email) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        viewCheck(question, email);
        return QuestionViewDto.from(question);
    }

    private void viewCheck(Question question, String email) {
        Optional<QuestionEmailToken> optionalQuestionEmailToken = questionEmailRepository.findById(question.getId());
        if (optionalQuestionEmailToken.isPresent() && optionalQuestionEmailToken.get().getEmail().equals(email)) {
            return;
        }
        
        QuestionEmailToken token = QuestionEmailToken.builder()
                .id(question.getId())
                .email(email)
                .build();
        question.increasedViews();

        questionRepository.save(question);
        questionEmailRepository.save(token);
    }

    @Override
    public void likeStatus(Long id, String email) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        Optional<QuestionLike> optionalQuestionLike = questionLikeRepository.findByQuestionIdAndMemberEmail(question, member);

        if (optionalQuestionLike.isPresent()) {
            question.subLike();
            questionLikeRepository.delete(optionalQuestionLike.get());
            questionRepository.save(question);
            return;
        }

        QuestionLike questionLike = QuestionLike.builder()
                .questionId(question)
                .memberEmail(member)
                .build();
        question.addLike();
        questionLikeRepository.save(questionLike);
        questionRepository.save(question);
    }

}
