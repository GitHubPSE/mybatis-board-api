package com.mybatis_crud.board.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDto {
    private Long id;
    private String title;
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
