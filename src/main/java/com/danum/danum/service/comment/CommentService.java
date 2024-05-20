package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.CommentDeleteDto;
import com.danum.danum.domain.comment.CommentNewDto;
import com.danum.danum.domain.comment.CommentUpdateDto;
import com.danum.danum.domain.comment.CommentViewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    public void created(CommentNewDto commentNewDto);

    public List<CommentViewDto> commentView(Long id);

    public void update(CommentUpdateDto commentUpdateDto);

    public void delete(CommentDeleteDto commentDeleteDto);

}
