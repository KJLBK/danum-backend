package com.danum.danum.service.board.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionMapper;
import com.danum.danum.domain.board.question.QuestionNewDto;
import com.danum.danum.domain.board.question.view.QuestionView;
import com.danum.danum.domain.board.question.view.QuestionViewMapper;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.QuestionRepository;
import com.danum.danum.repository.QuestionViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    private final QuestionViewRepository questionViewRepository;

    private final QuestionViewMapper questionViewMapper;

    @Override
    @Transactional
    public void created(QuestionNewDto questionNewDto) {
        if (questionNewDto.getEmail().isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }
        Question question = questionMapper.toEntity(questionNewDto);

        questionRepository.save(question);
    }

    @Override
    @Transactional
    public List<Question> viewList() {
        return questionRepository.findAll();
    }

    @Override
    @Transactional
    public Question view(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = question.getEmail();
        Optional<QuestionView> optionalQuestionViews = questionViewRepository.findByQuestionAndMember(question, member);
        if (optionalQuestionViews.isEmpty()) {
            QuestionView questionView = questionViewMapper.toEntity(question, member);
            questionViewRepository.save(questionView);
            question.addView();
            return questionRepository.save(question);
        }

        return question;
    }

    @Override
    @Transactional
    public boolean updateLike(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = question.getEmail();
        QuestionView questionView = questionViewRepository.findByQuestionAndMember(question, member)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        questionView.toggleLiked();
        questionViewRepository.save(questionView);
        if (questionView.isLiked()) {
            question.addLike();
            questionRepository.save(question);
            return true;
        }
        question.subLike();
        questionRepository.save(question);
        return false;
    }

}
