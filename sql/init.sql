CREATE TABLE board (
                       id          BIGSERIAL       NOT NULL,
                       title       VARCHAR(100)    NOT NULL,
                       content     TEXT            NOT NULL,
                       author      VARCHAR(30)     NOT NULL,
                       view_count  INT             DEFAULT 0   NOT NULL,
                       reg_date    TIMESTAMP       DEFAULT NOW() NOT NULL,
                       update_date TIMESTAMP       NULL,
                       del_yn      CHAR(1)         DEFAULT 'N' NOT NULL,
                       password    CHAR(4)         NOT NULL,
                       CONSTRAINT pk_board PRIMARY KEY (id)
);

INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 1', '테스트 내용 1', '작성자1', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 2', '테스트 내용 2', '작성자2', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 3', '테스트 내용 3', '작성자3', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 4', '테스트 내용 4', '작성자4', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 5', '테스트 내용 5', '작성자5', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 6', '테스트 내용 6', '작성자6', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 7', '테스트 내용 7', '작성자7', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 8', '테스트 내용 8', '작성자8', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 9', '테스트 내용 9', '작성자9', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 10', '테스트 내용 10', '작성자10', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 11', '테스트 내용 11', '작성자11', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 12', '테스트 내용 12', '작성자12', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 13', '테스트 내용 13', '작성자13', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 14', '테스트 내용 14', '작성자14', '1234');
INSERT INTO board (title, content, author, password) VALUES ('테스트 제목 15', '테스트 내용 15', '작성자15', '1234');