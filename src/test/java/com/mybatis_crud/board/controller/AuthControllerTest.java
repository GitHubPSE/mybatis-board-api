package com.mybatis_crud.board.controller;

import tools.jackson.databind.ObjectMapper;
import com.mybatis_crud.board.dto.AuthRequest;
import com.mybatis_crud.board.security.JwtAuthenticationFilter;
import com.mybatis_crud.board.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // SecurityConfig가 생성자에서 요구하는 빈 - BoardControllerTest와 같은 이유로 목 처리
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AuthRequest request(String id, String password) {
        AuthRequest request = new AuthRequest();
        request.setId(id);
        request.setPassword(password);
        return request;
    }

    @Test
    void signup_요청바디로_가입하고_200을_반환한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("user1", "1234"))))
                .andExpect(status().isOk());

        verify(authService).signUp(eq("user1"), eq("1234"));
    }

    @Test
    void signup_이미_존재하는_아이디면_409를_반환한다() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User already exists"))
                .when(authService).signUp("user1", "1234");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("user1", "1234"))))
                .andExpect(status().isConflict());
    }

    @Test
    void login_성공하면_토큰을_응답한다() throws Exception {
        when(authService.login("user1", "1234")).thenReturn("token123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("user1", "1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }
}
