package com.mybatis_crud.board.mapper;

import com.mybatis_crud.board.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserMapper userMapper;

    private UserDto newUser(String id) {
        UserDto user = new UserDto();
        user.setId(id);
        user.setPassword("hashed-password");
        return user;
    }

    @Test
    void insertUser_등록후_findById로_조회된다() {
        userMapper.insertUser(newUser("user1"));

        UserDto result = userMapper.findById("user1");

        assertThat(result.getId()).isEqualTo("user1");
        assertThat(result.getPassword()).isEqualTo("hashed-password");
        assertThat(result.getRegDate()).isNotNull();
    }

    @Test
    void findById_없는_아이디면_null을_반환한다() {
        UserDto result = userMapper.findById("no-such-user");

        assertThat(result).isNull();
    }

    @Test
    void insertUser_이미_존재하는_아이디면_예외가_발생한다() {
        userMapper.insertUser(newUser("user1"));

        assertThatThrownBy(() -> userMapper.insertUser(newUser("user1")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
