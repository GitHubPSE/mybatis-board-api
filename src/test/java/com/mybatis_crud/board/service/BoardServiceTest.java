package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.mapper.BoardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardService boardService;

    private BoardDto boardOwnedBy(String userId) {
        BoardDto board = new BoardDto();
        board.setId(1L);
        board.setUserId(userId);
        return board;
    }

    @Test
    void getBoardList_계산된_offset과_totalPages를_반환한다() {
        when(boardMapper.getBoardCount(any(BoardDto.class))).thenReturn(25);
        when(boardMapper.getBoardList(any(BoardDto.class))).thenReturn(List.of(new BoardDto()));

        Map<String, Object> result = boardService.getBoardList(2, 10, null, null);

        assertThat(result.get("page")).isEqualTo(2);
        assertThat(result.get("totalPages")).isEqualTo(3);
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
    void insertBoard_로그인아이디를_userId로_설정해서_등록한다() {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle("제목");

        boardService.insertBoard(boardDto, "user1");

        verify(boardMapper).insertBoard(argThat(dto -> "user1".equals(dto.getUserId())));
    }

    @Test
    void updateBoard_작성자가_본인이면_매퍼의_updateBoard를_호출한다() {
        when(boardMapper.getBoardDetail(1L)).thenReturn(boardOwnedBy("user1"));
        BoardDto updateDto = new BoardDto();
        updateDto.setId(1L);
        updateDto.setTitle("수정된제목");

        boardService.updateBoard(updateDto, "user1");

        verify(boardMapper).updateBoard(updateDto);
    }

    @Test
    void updateBoard_작성자가_아니면_AccessDeniedException을_던진다() {
        when(boardMapper.getBoardDetail(1L)).thenReturn(boardOwnedBy("user1"));
        BoardDto updateDto = new BoardDto();
        updateDto.setId(1L);

        assertThatThrownBy(() -> boardService.updateBoard(updateDto, "user2"))
                .isInstanceOf(AccessDeniedException.class);
        verify(boardMapper, never()).updateBoard(any());
    }

    @Test
    void updateBoard_게시글이_없으면_404_예외를_던진다() {
        when(boardMapper.getBoardDetail(1L)).thenReturn(null);
        BoardDto updateDto = new BoardDto();
        updateDto.setId(1L);

        assertThatThrownBy(() -> boardService.updateBoard(updateDto, "user1"))
                .isInstanceOf(ResponseStatusException.class);
        verify(boardMapper, never()).updateBoard(any());
    }

    @Test
    void deleteBoard_작성자가_본인이면_매퍼의_deleteBoard를_호출한다() {
        when(boardMapper.getBoardDetail(1L)).thenReturn(boardOwnedBy("user1"));

        boardService.deleteBoard(1L, "user1");

        verify(boardMapper).deleteBoard(1L);
    }

    @Test
    void deleteBoard_작성자가_아니면_AccessDeniedException을_던진다() {
        when(boardMapper.getBoardDetail(1L)).thenReturn(boardOwnedBy("user1"));

        assertThatThrownBy(() -> boardService.deleteBoard(1L, "user2"))
                .isInstanceOf(AccessDeniedException.class);
        verify(boardMapper, never()).deleteBoard(any());
    }
}
