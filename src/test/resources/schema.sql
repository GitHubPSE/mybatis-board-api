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
