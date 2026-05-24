package com.mybatis_crud.board.controller;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBoardList(@RequestParam int page, @RequestParam int pageSize) {
        return ResponseEntity.ok(boardService.getBoardList(page, pageSize));
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoardDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardDetail(id));
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<Void> insertBoard(@RequestBody BoardDto boardDto) {
        boardService.insertBoard(boardDto);
        return ResponseEntity.ok().build();
    }

    // 게시글 수정
    @PutMapping("/{id}/update")
    public ResponseEntity<Void> updateBoard(@PathVariable Long id, @RequestBody BoardDto boardDto) {
        boardDto.setId(id);
        boardService.updateBoard(boardDto);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> patchBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 확인
    @PostMapping("/{id}/password")
    public ResponseEntity<Boolean> passwordCheck(@PathVariable Long id, @RequestBody BoardDto boardDto) {
        boolean result = boardService.passwordCheck(id, boardDto.getPassword());
        return ResponseEntity.ok(result);
    }
}
