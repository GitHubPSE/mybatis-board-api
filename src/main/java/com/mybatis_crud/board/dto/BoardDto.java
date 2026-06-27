package com.mybatis_crud.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDto {
    private Long id;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private String userId;
    private int viewCount;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private String delYn;
    private int pageSize;
    private int offset;
    private String keyword;
    private String searchType;
}
