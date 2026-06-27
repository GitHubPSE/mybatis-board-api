package com.mybatis_crud.board.mapper;

import com.mybatis_crud.board.dto.BoardDto;
import com.mybatis_crud.board.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardMapperTest {

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
    private BoardMapper boardMapper;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUpUser() {
        // board.user_id가 users.id를 참조(FK)하므로, 게시글을 등록하기 전에 사용자가 먼저 있어야 한다.
        UserDto user = new UserDto();
        user.setId("user1");
        user.setPassword("hashed-password");
        userMapper.insertUser(user);
    }

    private BoardDto newBoard(String title) {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent("내용");
        boardDto.setUserId("user1");
        return boardDto;
    }

    // 실제 서비스도 insert 후 id를 돌려받지 않고 목록 조회로 id를 알아내므로, 테스트도 동일한 방식으로 id를 구한다.
    private Long findIdByTitle(String title) {
        BoardDto pageRequest = new BoardDto();
        pageRequest.setPageSize(100);
        pageRequest.setOffset(0);
        return boardMapper.getBoardList(pageRequest).stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    @Test
    void insertBoard_등록후_getBoardDetail로_조회된다() {
        boardMapper.insertBoard(newBoard("제목1"));
        Long id = findIdByTitle("제목1");

        BoardDto result = boardMapper.getBoardDetail(id);

        assertThat(result.getTitle()).isEqualTo("제목1");
        assertThat(result.getUserId()).isEqualTo("user1");
        assertThat(result.getViewCount()).isZero();
    }

    @Test
    void getBoardList_del_yn이_N인_게시글만_id_내림차순으로_조회된다() {
        boardMapper.insertBoard(newBoard("제목1"));
        boardMapper.insertBoard(newBoard("제목2"));
        boardMapper.insertBoard(newBoard("제목3"));
        boardMapper.deleteBoard(findIdByTitle("제목2"));

        BoardDto pageRequest = new BoardDto();
        pageRequest.setPageSize(10);
        pageRequest.setOffset(0);
        List<BoardDto> list = boardMapper.getBoardList(pageRequest);

        assertThat(list).extracting(BoardDto::getTitle).containsExactly("제목3", "제목1");
    }

    @Test
    void getBoardList_offset과_pageSize로_페이징된다() {
        for (int i = 1; i <= 5; i++) {
            boardMapper.insertBoard(newBoard("제목" + i));
        }

        BoardDto pageRequest = new BoardDto();
        pageRequest.setPageSize(2);
        pageRequest.setOffset(2);
        List<BoardDto> list = boardMapper.getBoardList(pageRequest);

        assertThat(list).extracting(BoardDto::getTitle).containsExactly("제목3", "제목2");
    }

    @Test
    void getBoardCount_del_yn이_N인_게시글만_카운트한다() {
        boardMapper.insertBoard(newBoard("제목1"));
        boardMapper.insertBoard(newBoard("제목2"));
        boardMapper.deleteBoard(findIdByTitle("제목2"));

        int count = boardMapper.getBoardCount(new BoardDto());

        assertThat(count).isEqualTo(1);
    }

    @Test
    void getBoardDetail_삭제된_게시글은_조회되지_않는다() {
        boardMapper.insertBoard(newBoard("제목1"));
        Long id = findIdByTitle("제목1");
        boardMapper.deleteBoard(id);

        BoardDto result = boardMapper.getBoardDetail(id);

        assertThat(result).isNull();
    }

    @Test
    void updateBoard_제목과_내용이_수정된다() {
        boardMapper.insertBoard(newBoard("원래제목"));
        Long id = findIdByTitle("원래제목");

        BoardDto updateDto = new BoardDto();
        updateDto.setId(id);
        updateDto.setTitle("수정된제목");
        updateDto.setContent("수정된내용");
        boardMapper.updateBoard(updateDto);

        BoardDto result = boardMapper.getBoardDetail(id);
        assertThat(result.getTitle()).isEqualTo("수정된제목");
        assertThat(result.getContent()).isEqualTo("수정된내용");
        assertThat(result.getUpdateDate()).isNotNull();
    }

    @Test
    void viewCountPlus_조회수가_1_증가한다() {
        boardMapper.insertBoard(newBoard("제목1"));
        Long id = findIdByTitle("제목1");

        boardMapper.viewCountPlus(id);
        boardMapper.viewCountPlus(id);

        BoardDto result = boardMapper.getBoardDetail(id);
        assertThat(result.getViewCount()).isEqualTo(2);
    }
}
