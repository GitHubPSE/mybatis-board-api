package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.mapper.BoardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardService boardService;

    @Test
    void getBoardList_계산된_offset과_totalPages를_반환한다() {
        when(boardMapper.getBoardCount()).thenReturn(25);
        when(boardMapper.getBoardList(any(BoardDto.class))).thenReturn(List.of(new BoardDto()));

        Map<String, Object> result = boardService.getBoardList(2, 10);

        assertThat(result.get("page")).isEqualTo(2);
        assertThat(result.get("totalPages")).isEqualTo(99);
        assertThat((List<?>) result.get("list")).hasSize(1);
        verify(boardMapper).getBoardList(argThat(dto -> dto.getOffset() == 10 && dto.getPageSize() == 10));
    }

    @Test
    void getBoardDetail_조회수를_증가시키고_상세정보를_반환한다() {
        BoardDto boardDto = new BoardDto();
        boardDto.setId(1L);
        boardDto.setTitle("제목");
        when(boardMapper.getBoardDetail(1L)).thenReturn(boardDto);

        BoardDto result = boardService.getBoardDetail(1L);

        verify(boardMapper).viewCountPlus(1L);
        assertThat(result.getTitle()).isEqualTo("제목");
    }

    @Test
    void insertBoard_매퍼의_insertBoard를_호출한다() {
        BoardDto boardDto = new BoardDto();

        boardService.insertBoard(boardDto);

        verify(boardMapper).insertBoard(boardDto);
    }

    @Test
    void updateBoard_매퍼의_updateBoard를_호출한다() {
        BoardDto boardDto = new BoardDto();

        boardService.updateBoard(boardDto);

        verify(boardMapper).updateBoard(boardDto);
    }

    @Test
    void deleteBoard_매퍼의_deleteBoard를_호출한다() {
        boardService.deleteBoard(1L);

        verify(boardMapper).deleteBoard(1L);
    }

    @Test
    void passwordCheck_비밀번호가_일치하면_true를_반환한다() {
        when(boardMapper.passwordCheck(1L)).thenReturn("1234");

        boolean result = boardService.passwordCheck(1L, "1234");

        assertThat(result).isTrue();
    }

    @Test
    void passwordCheck_비밀번호가_다르면_false를_반환한다() {
        when(boardMapper.passwordCheck(1L)).thenReturn("1234");

        boolean result = boardService.passwordCheck(1L, "9999");

        assertThat(result).isFalse();
    }

    @Test
    void passwordCheck_게시글이_없으면_false를_반환한다() {
        when(boardMapper.passwordCheck(1L)).thenReturn(null);

        boolean result = boardService.passwordCheck(1L, "1234");

        assertThat(result).isFalse();
    }
}
