package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.UserDto;
import com.mybatis_crud.board.mapper.UserMapper;
import com.mybatis_crud.board.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_새_아이디면_비밀번호를_암호화해서_저장한다() {
        when(userMapper.findById("user1")).thenReturn(null);
        when(passwordEncoder.encode("1234")).thenReturn("encoded-password");

        authService.signUp("user1", "1234");

        verify(userMapper).insertUser(argThat(user ->
                "user1".equals(user.getId()) && "encoded-password".equals(user.getPassword())));
    }

    @Test
    void signUp_이미_존재하는_아이디면_예외를_던지고_저장하지_않는다() {
        when(userMapper.findById("user1")).thenReturn(new UserDto());

        assertThatThrownBy(() -> authService.signUp("user1", "1234"))
                .isInstanceOf(ResponseStatusException.class);
        verify(userMapper, never()).insertUser(any());
    }

    @Test
    void login_인증에_성공하면_토큰을_반환한다() {
        when(jwtUtil.generateToken("user1")).thenReturn("token123");

        String token = authService.login("user1", "1234");

        assertThat(token).isEqualTo("token123");
        verify(authenticationManager).authenticate(
                argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken
                        && "user1".equals(auth.getName())));
    }
}
