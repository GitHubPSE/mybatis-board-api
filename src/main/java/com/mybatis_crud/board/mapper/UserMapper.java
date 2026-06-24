package com.mybatis_crud.board.mapper;

import com.mybatis_crud.board.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserDto findById(String id);
    void insertUser(UserDto userDto);
}
