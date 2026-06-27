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

INSERT INTO board (title, content, user_id) VALUES ('인터스텔라', '지구 멸망이 가까워지자 전직 우주비행사가 새로운 터전을 찾기 위해 웜홀을 통해 우주로 떠난다. 사랑과 시간, 중력을 넘나드는 여정을 그린 SF 대작.', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('기생충', '반지하에 사는 기택 가족이 부유한 박 사장 가족에게 하나씩 스며들면서 벌어지는 예측불허의 사건을 그린 블랙코미디 스릴러.', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('올드보이', '이유도 모른 채 15년간 감금된 남자가 풀려난 뒤 진실을 추적하는 과정에서 충격적인 비밀과 마주한다.', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('매트릭스', '평범한 프로그래머가 현실이 거대한 가상 시뮬레이션임을 알게 되고, 인류를 지배하는 기계에 맞서 싸우는 SF 액션.', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('쇼생크 탈출', '억울하게 종신형을 선고받은 은행가가 교도소에서 희망을 잃지 않고 자유를 향해 나아가는 감동적인 이야기.', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('겨울왕국', '얼음을 다루는 마법을 가진 엘사와 그녀를 찾아 떠나는 동생 안나의 모험을 담은 애니메이션. 진정한 사랑의 의미를 되새기게 한다.', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('다크 나이트', '조커라는 카오스의 화신이 고담시를 혼란에 빠뜨리고, 배트맨은 자신의 신념과 한계 사이에서 갈등한다.', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('어벤져스: 엔드게임', '타노스에게 절반의 생명을 잃은 우주. 살아남은 히어로들이 마지막 희망을 걸고 시간을 거슬러 반격에 나선다.', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('라라랜드', '꿈을 좇는 재즈 피아니스트와 배우 지망생의 달콤쌉싸름한 사랑 이야기. 현실과 꿈 사이의 선택을 뮤지컬로 풀어냈다.', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('아이언맨', '천재 억만장자 토니 스타크가 테러리스트에 납치된 뒤 아이언맨 슈트를 만들어 탈출하고 히어로로 거듭나는 이야기.', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('명량', '1597년 단 12척의 배로 330척의 왜군에 맞선 이순신 장군의 명량해전을 그린 역사 대작.', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('토이 스토리', '장난감들이 주인 아이의 눈을 피해 살아 움직이며 벌이는 모험. 버즈와 우디의 우정이 감동을 준다.', 'user3');
INSERT INTO board (title, content, user_id) VALUES ('존 윅', '은퇴한 전설적인 킬러가 소중한 강아지를 잃고 복수에 나서며 언더월드 전체를 상대로 싸우는 액션 스릴러.', 'user1');
INSERT INTO board (title, content, user_id) VALUES ('헤어질 결심', '살인 사건을 수사하던 형사가 용의자 여성에게 알 수 없는 감정을 품게 되며 진실과 감정 사이에서 흔들린다.', 'user2');
INSERT INTO board (title, content, user_id) VALUES ('부산행', '좀비 바이러스가 퍼진 한국에서 부산행 KTX에 갇힌 사람들의 사투를 그린 재난 액션 영화.', 'user3');
