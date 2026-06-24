package com.mybatis_crud.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String id;
    private String password;
}
