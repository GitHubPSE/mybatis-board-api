package com.mybatis_crud.board.mapper;

import com.mybatis_crud.board.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardDto> getBoardList(BoardDto boardDto);
    BoardDto getBoardDetail(Long id);
    void insertBoard(BoardDto boardDto);
    void updateBoard(BoardDto boardDto);
    void deleteBoard(Long id);
    void viewCountPlus(Long id);
    String passwordCheck(Long id);
    int getBoardCount();
}
