CREATE TABLE users (
    id          VARCHAR(30)     NOT NULL,
    password    VARCHAR(100)    NOT NULL,
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
