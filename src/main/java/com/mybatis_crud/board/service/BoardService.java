package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void insertBoard(BoardDto boardDto) {
        boardMapper.insertBoard(boardDto);
    }

    // 게시글 수정
    public void updateBoard(BoardDto boardDto) {
        boardMapper.updateBoard(boardDto);
    }

    // 게시글 삭제
    public void deleteBoard(Long id) {
        boardMapper.deleteBoard(id);
    }

    // 비밀번호 확인
    public boolean passwordCheck(Long id, String password) {
        String storedPassword = boardMapper.passwordCheck(id);
        if (storedPassword != null && storedPassword.trim().equals(password)){
            return true;
        } else {
            return false;
        }
    }
}
