package com.mybatis_crud.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "아이디는 3~20자의 영문/숫자/-/_ 만 사용할 수 있습니다.")
    private String id;

    @NotBlank
    @Size(min = 4, max = 50)
    private String password;
}
