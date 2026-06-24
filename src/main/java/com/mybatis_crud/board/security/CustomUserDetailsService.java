package com.mybatis_crud.board.security;

import com.mybatis_crud.board.dto.UserDto;
import com.mybatis_crud.board.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        UserDto user = userMapper.findById(id);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return User.withUsername(id).password(user.getPassword()).build();
    }
}
