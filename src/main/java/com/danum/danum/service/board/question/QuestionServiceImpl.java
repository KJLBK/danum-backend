package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.*;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.QuestionEmailRepository;
import com.danum.danum.repository.board.QuestionLikeRepository;
import com.danum.danum.repository.board.QuestionRepository;
import com.danum.danum.repository.comment.QuestionCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    private final QuestionEmailRepository questionEmailRepository;

    private final MemberRepository memberRepository;

    private final QuestionLikeRepository questionLikeRepository;

    private final QuestionCommentRepository questionCommentRepository;

    @Override
    @Transactional
    public void create(QuestionNewDto questionNewDto) {
        memberRepository.findById(questionNewDto.getEmail())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        Question question = questionMapper.toEntity(questionNewDto);

        questionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> viewList(Pageable pageable) {
        Page<Question> questionPage = questionRepository.findAll(pageable);
        return questionPage.map(QuestionViewDto::from);
    }

    @Override
    @Transactional
    public QuestionViewDto view(Long id, String email) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));

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

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public void update(QuestionUpdateDto questionUpdateDto, String loginUser) {
        Question question = questionRepository.findById(questionUpdateDto.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        userCheck(question.getMember().getEmail(), loginUser);
        question.update(questionUpdateDto.getContent(), questionUpdateDto.getTitle());

        questionRepository.save(question);
    }

    private void userCheck(String author, String loginUser) {
        if (!author.equals(loginUser)) {
            throw new BoardException(ErrorCode.BOARD_NOT_AUTHOR_EXCEPTION);
        }
    }

    @Override
    public List<QuestionViewDto> getPopularQuestions(int limit) {
        return questionRepository.findPopularQuestions(PageRequest.of(0, limit))
                .stream()
                .map(QuestionViewDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAcceptedComment(Long questionId) {
        return questionCommentRepository.existsByQuestionAndIsAcceptedTrue(questionRepository.findById(questionId)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> getQuestionsByRegion(String city, String district, Pageable pageable) {
        RegionSearchStrategy strategy = RegionSearchStrategy.getStrategy(city, district);
        Page<Question> questionPage = strategy.search(questionRepository, city, district, pageable);
        return questionPage.map(QuestionViewDto::from);
    }
}

enum RegionSearchStrategy {
    ALL {
        @Override
        Page<Question> search(QuestionRepository repo, String city, String district, Pageable pageable) {
            return repo.findAll(pageable);
        }
    },
    CITY {
        @Override
        Page<Question> search(QuestionRepository repo, String city, String district, Pageable pageable) {
            return repo.findByAddressTagStartingWith(city, pageable);
        }
    },
    DISTRICT {
        @Override
        Page<Question> search(QuestionRepository repo, String city, String district, Pageable pageable) {
            String fullRegion = String.join(" ", city, district);
            return repo.findByAddressTagStartingWith(fullRegion, pageable);
        }
    };

    abstract Page<Question> search(QuestionRepository repo, String city, String district, Pageable pageable);

    static RegionSearchStrategy getStrategy(String city, String district) {
        return Optional.ofNullable(city)
                .map(c -> Optional.ofNullable(district).map(d -> DISTRICT).orElse(CITY))
                .orElse(ALL);
    }
}
