package com.mybatis_crud.board.controller;

import tools.jackson.databind.ObjectMapper;
import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    @Test
    void getBoardList_목록과_페이지정보를_반환한다() throws Exception {
        BoardDto board = new BoardDto();
        board.setId(1L);
        board.setTitle("제목1");
        when(boardService.getBoardList(1, 10))
                .thenReturn(Map.of("list", List.of(board), "page", 1, "totalPages", 1));

        mockMvc.perform(get("/api/board/list").param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.list[0].title").value("제목1"));
    }

    @Test
    void getBoardDetail_id로_상세정보를_반환한다() throws Exception {
        BoardDto board = new BoardDto();
        board.setId(1L);
        board.setTitle("제목1");
        board.setContent("내용1");
        when(boardService.getBoardDetail(1L)).thenReturn(board);

        mockMvc.perform(get("/api/board/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목1"))
                .andExpect(jsonPath("$.content").value("내용1"));
    }

    @Test
    void insertBoard_요청바디로_등록하고_200을_반환한다() throws Exception {
        BoardDto request = new BoardDto();
        request.setTitle("제목1");
        request.setContent("내용1");
        request.setAuthor("작성자1");
        request.setPassword("1234");

        mockMvc.perform(post("/api/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(boardService).insertBoard(any(BoardDto.class));
    }

    @Test
    void updateBoard_경로의_id를_dto에_설정하여_수정한다() throws Exception {
        BoardDto request = new BoardDto();
        request.setTitle("수정된제목");
        request.setContent("수정된내용");
        request.setAuthor("작성자1");
        request.setPassword("1234");

        mockMvc.perform(put("/api/board/{id}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(boardService).updateBoard(argThat(dto -> dto.getId().equals(1L) && dto.getTitle().equals("수정된제목")));
    }

    @Test
    void deleteBoard_id로_삭제를_요청한다() throws Exception {
        mockMvc.perform(patch("/api/board/{id}/delete", 1L))
                .andExpect(status().isOk());

        verify(boardService).deleteBoard(1L);
    }

    @Test
    void passwordCheck_일치하면_true를_반환한다() throws Exception {
        when(boardService.passwordCheck(1L, "1234")).thenReturn(true);
        BoardDto request = new BoardDto();
        request.setPassword("1234");

        mockMvc.perform(post("/api/board/{id}/password", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
