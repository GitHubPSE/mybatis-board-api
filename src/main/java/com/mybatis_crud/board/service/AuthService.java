package com.mybatis_crud.board.service;

import com.mybatis_crud.board.dto.UserDto;
import com.mybatis_crud.board.mapper.UserMapper;
import com.mybatis_crud.board.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void signUp(String id, String password){
        UserDto existing = userMapper.findById(id);

        if(existing != null){ throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        UserDto newUser = new UserDto();
        newUser.setId(id);
        newUser.setPassword(passwordEncoder.encode(password));

        userMapper.insertUser(newUser);
    }

    public String login(String id, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, password));

        return jwtUtil.generateToken(id);
    }
}
