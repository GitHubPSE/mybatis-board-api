package com.mybatis_crud.board.controller;

import com.mybatis_crud.board.dto.AuthRequest;
import com.mybatis_crud.board.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody AuthRequest request){
        authService.signUp(request.getId(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request){
        String token = authService.login(request.getId(), request.getPassword());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
