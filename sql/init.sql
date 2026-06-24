CREATE TABLE users (
    id          VARCHAR(30)     NOT NULL,  -- 로그인 아이디
    password    VARCHAR(100)    NOT NULL,  -- BCrypt 해시
    reg_date    TIMESTAMP       DEFAULT NOW() NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE board (
    id          BIGSERIAL       NOT NULL,
    title       VARCHAR(100)    NOT NULL,
    content     TEXT            NOT NULL,
    user_id     VARCHAR(30)     NOT NULL,
    view_count  INT             DEFAULT 0   NOT NULL,
    reg_date    TIMESTAMP       DEFAULT NOW() NOT NULL,
    update_date TIMESTAMP       NULL,
    del_yn      CHAR(1)         DEFAULT 'N' NOT NULL,
    CONSTRAINT pk_board PRIMARY KEY (id),
    CONSTRAINT fk_board_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 샘플 계정 (비밀번호는 전부 '1234'의 BCrypt 해시)
INSERT INTO users (id, password) VALUES ('user1', '$2y$10$18MMrTft7aGa8Z7OXDRJdOKiGa.2ZHqqpYWMOwQnExc.1vIaJDmdu');
INSERT INTO users (id, password) VALUES ('user2', '$2y$10$18MMrTft7aGa8Z7OXDRJdOKiGa.2ZHqqpYWMOwQnExc.1vIaJDmdu');
INSERT INTO users (id, password) VALUES ('user3', '$2y$10$18MMrTft7aGa8Z7OXDRJdOKiGa.2ZHqqpYWMOwQnExc.1vIaJDmdu');

INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 1', '테스트 내용 1', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 2', '테스트 내용 2', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 3', '테스트 내용 3', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 4', '테스트 내용 4', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 5', '테스트 내용 5', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 6', '테스트 내용 6', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 7', '테스트 내용 7', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 8', '테스트 내용 8', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 9', '테스트 내용 9', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 10', '테스트 내용 10', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 11', '테스트 내용 11', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 12', '테스트 내용 12', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 13', '테스트 내용 13', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 14', '테스트 내용 14', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('테스트 제목 15', '테스트 내용 15', 'user3');
