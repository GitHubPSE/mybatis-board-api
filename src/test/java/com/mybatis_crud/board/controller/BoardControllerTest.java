package com.mybatis_crud.board.controller;

import tools.jackson.databind.ObjectMapper;
import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.security.JwtAuthenticationFilter;
import com.mybatis_crud.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    // SecurityConfig가 생성자에서 요구하는 빈인데, @WebMvcTest 슬라이스에는
    // JwtAuthenticationFilter(→ JwtUtil, CustomUserDetailsService → UserMapper)가 없어서
    // 컨텍스트 로딩이 실패한다. addFilters=false로 필터 적용은 막아도 빈 자체는 필요하므로 목으로 채워준다.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
    @WithMockUser(username = "user1")
    void insertBoard_요청바디로_등록하고_200을_반환한다() throws Exception {
        BoardDto request = new BoardDto();
        request.setTitle("제목1");
        request.setContent("내용1");

        mockMvc.perform(post("/api/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(boardService).insertBoard(any(BoardDto.class), eq("user1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void updateBoard_경로의_id를_dto에_설정하여_수정한다() throws Exception {
        BoardDto request = new BoardDto();
        request.setTitle("수정된제목");
        request.setContent("수정된내용");

        mockMvc.perform(put("/api/board/{id}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(boardService).updateBoard(
                argThat(dto -> dto.getId().equals(1L) && dto.getTitle().equals("수정된제목")),
                eq("user1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteBoard_id로_삭제를_요청한다() throws Exception {
        mockMvc.perform(patch("/api/board/{id}/delete", 1L))
                .andExpect(status().isOk());

        verify(boardService).deleteBoard(1L, "user1");
    }
}
