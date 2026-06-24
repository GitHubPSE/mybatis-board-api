package com.mybatis_crud.board.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private String id;       // 로그인 아이디, 기본키
    private String password;
    private LocalDateTime regDate;
}
