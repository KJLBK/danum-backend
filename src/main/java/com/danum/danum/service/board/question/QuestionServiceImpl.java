package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.*;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.*;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final QuestionEmailRepository questionEmailRepository;
    private final MemberRepository memberRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final QuestionCommentRepository questionCommentRepository;

    @Override
    @Transactional
    public QuestionViewDto create(QuestionNewDto questionNewDto) {
        findMemberByEmail(questionNewDto.getEmail());
        Question question = questionMapper.toEntity(questionNewDto);
        Question savedQuestion = questionRepository.save(question);
        return QuestionViewDto.from(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> searchQuestions(String keyword, Pageable pageable) {
        return questionRepository.searchByKeyword(keyword, pageable)
                .map(QuestionViewDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> viewList(Pageable pageable) {
        return questionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(QuestionViewDto::from);
    }

    @Override
    @Transactional
    public QuestionViewDto view(Long id, String email) {
        Question question = findQuestionById(id);
        processViewCount(question, email);
        return convertToViewDto(question);
    }

    @Override
    @Transactional
    public void likeStatus(Long id, String email) {
        Question question = findQuestionById(id);
        Member member = findMemberByEmail(email);
        processLikeStatus(question, member);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question question = findQuestionById(id);
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public void update(QuestionUpdateDto questionUpdateDto, String loginUser) {
        Question question = findQuestionById(questionUpdateDto.getId());
        validateAuthor(question.getMember().getEmail(), loginUser);
        updateQuestionContent(question, questionUpdateDto);
    }

    @Override
    public List<QuestionViewDto> getPopularQuestions(int limit) {
        return questionRepository.findPopularQuestions(PageRequest.of(0, limit))
                .stream()
                .map(this::convertToViewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAcceptedComment(Long questionId) {
        Question question = findQuestionById(questionId);
        return questionCommentRepository.existsByQuestionAndIsAcceptedTrue(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionViewDto> getQuestionsByRegion(String city, String district, Pageable pageable) {
        RegionSearchStrategy strategy = RegionSearchStrategy.getStrategy(city, district);
        return strategy.search(questionRepository, city, district, pageable)
                .map(this::convertToViewDto);
    }

    // Private helper methods
    private Question findQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    private void processViewCount(Question question, String email) {
        boolean hasAlreadyViewed = questionEmailRepository.findById(question.getId())
                .map(token -> token.getEmail().equals(email))
                .orElse(false);

        if (!hasAlreadyViewed) {
            saveNewView(question, email);
        }
    }

    private void saveNewView(Question question, String email) {
        QuestionEmailToken token = QuestionEmailToken.builder()
                .id(question.getId())
                .email(email)
                .build();

        question.increasedViews();
        questionRepository.save(question);
        questionEmailRepository.save(token);
    }

    private void processLikeStatus(Question question, Member member) {
        Optional<QuestionLike> existingLike = questionLikeRepository
                .findByQuestionIdAndMemberEmail(question, member);

        existingLike.ifPresentOrElse(
                like -> removeLike(question, like),
                () -> addLike(question, member)
        );
    }

    private void removeLike(Question question, QuestionLike like) {
        question.subLike();
        questionLikeRepository.delete(like);
        questionRepository.save(question);
    }

    private void addLike(Question question, Member member) {
        QuestionLike questionLike = QuestionLike.builder()
                .questionId(question)
                .memberEmail(member)
                .build();

        question.addLike();
        questionLikeRepository.save(questionLike);
        questionRepository.save(question);
    }

    private void validateAuthor(String author, String loginUser) {
        if (!author.equals(loginUser)) {
            throw new BoardException(ErrorCode.BOARD_NOT_AUTHOR_EXCEPTION);
        }
    }

    private void updateQuestionContent(Question question, QuestionUpdateDto updateDto) {
        question.update(updateDto.getContent(), updateDto.getTitle());
        questionRepository.save(question);
    }

    private QuestionViewDto convertToViewDto(Question question) {
        return QuestionViewDto.from(question);
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