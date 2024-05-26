package com.danum.danum.controller.board;

import com.danum.danum.domain.board.BoardNewDto;
import com.danum.danum.domain.board.BoardUpdateDto;
import com.danum.danum.domain.board.Category;
import com.danum.danum.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/board/new")
    public ResponseEntity<?> created(@RequestBody BoardNewDto boardNewDto){
        boardService.created(boardNewDto);

        return ResponseEntity.ok("게시판 생성 성공");
    }

    @GetMapping("/board/view/list/category/{id}")
    public ResponseEntity<?> boardViewList(@PathVariable("id") int category){
        return ResponseEntity.ok(boardService.boardViewList(category));
    }

    @GetMapping("/board/view/{id}")
    public ResponseEntity<?> boardView(@PathVariable("id") Long id){
        return ResponseEntity.ok(boardService.boardView(id));
    }

    @GetMapping("/board/search/keyword")
    public ResponseEntity<?> searchList(@RequestParam("keyword") String keyword){
        return ResponseEntity.ok(boardService.boardSearchList(keyword));
    }

    @PatchMapping("/board/update")
    public ResponseEntity<?> updateBoard(@RequestBody BoardUpdateDto boardCountDto) {
        boardService.updateBoard(boardCountDto);

        return ResponseEntity.ok("업데이트 성공");
    }

}
