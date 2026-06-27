package com.mybatis_crud.board.controller;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBoardList(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType) {
        return ResponseEntity.ok(boardService.getBoardList(page, pageSize, keyword, searchType));
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoardDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardDetail(id));
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<Void> insertBoard(@RequestBody BoardDto boardDto) {
        boardService.insertBoard(boardDto, currentLoginId());
        return ResponseEntity.ok().build();
    }

    // 게시글 수정 (작성자만 가능)
    @PutMapping("/{id}/update")
    public ResponseEntity<Void> updateBoard(@PathVariable Long id, @RequestBody BoardDto boardDto) {
        boardDto.setId(id);
        boardService.updateBoard(boardDto, currentLoginId());
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제 (작성자만 가능)
    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> patchBoard(@PathVariable Long id) {
        boardService.deleteBoard(id, currentLoginId());
        return ResponseEntity.ok().build();
    }

    // SecurityContext의 인증 주체 이름 = JWT subject = 로그인 아이디
    private String currentLoginId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
