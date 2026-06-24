package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    // 게시글 목록
    public Map<String, Object> getBoardList(int page, int pageSize) {
        BoardDto boardDto = new BoardDto();
        boardDto.setPageSize(pageSize);
        // offset -> 1페이지 : 0번째부터, 2페이지 : 10번째부터...
        boardDto.setOffset((page - 1) * pageSize);

        int totalCount = boardMapper.getBoardCount();
        int totalPages = (totalCount + pageSize - 1) / pageSize;

        Map<String, Object> result = new HashMap<>();
        result.put("list", boardMapper.getBoardList(boardDto));
        result.put("page", page);
        result.put("totalPages", totalPages);

        return result;
    }

    // 게시글 상세
    public BoardDto getBoardDetail(Long id) {
        boardMapper.viewCountPlus(id);
        return boardMapper.getBoardDetail(id);
    }

    // 게시글 등록
    public void insertBoard(BoardDto boardDto, String loginId) {
        boardDto.setUserId(loginId);
        boardMapper.insertBoard(boardDto);
    }

    // 게시글 수정
    public void updateBoard(BoardDto boardDto, String loginId) {
        checkOwner(boardDto.getId(), loginId);
        boardMapper.updateBoard(boardDto);
    }

    // 게시글 삭제
    public void deleteBoard(Long id, String loginId) {
        checkOwner(id, loginId);
        boardMapper.deleteBoard(id);
    }

    // 작성자 본인 확인 (작성자가 아니면 403)
    private void checkOwner(Long boardId, String loginId) {
        BoardDto board = boardMapper.getBoardDetail(boardId);
        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        if (!board.getUserId().equals(loginId)) {
            throw new AccessDeniedException("작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
