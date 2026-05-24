# Board

Spring Boot + MyBatis + PostgreSQL 기반의 게시판 CRUD 웹 애플리케이션입니다.

## 기술 스택

- **Backend**: Spring Boot 4.0.6, Java 17
- **ORM**: MyBatis 4.0.1
- **Database**: PostgreSQL
- **Frontend**: HTML, jQuery 3.7.0, Bootstrap 5.3.0
- **Build Tool**: Gradle

---

## 프로젝트 구조

```
board/
├── src/
│   ├── main/
│   │   ├── java/com/mybatis_crud/board/
│   │   │   ├── BoardApplication.java
│   │   │   ├── controller/
│   │   │   │   └── BoardController.java
│   │   │   ├── service/
│   │   │   │   └── BoardService.java
│   │   │   ├── mapper/
│   │   │   │   └── BoardMapper.java
│   │   │   └── dto/
│   │   │       └── BoardDto.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── mapper/
│   │       │   └── BoardMapper.xml
│   │       └── static/
│   │           ├── index.html    # 게시글 목록
│   │           ├── detail.html   # 게시글 상세
│   │           └── edit.html     # 게시글 작성/수정
├── sql/
│   ├── init.sql                  # 테이블 생성 및 샘플 데이터
│   └── ERD.png
├── build.gradle
└── settings.gradle
```

---

## DB 스키마

```sql
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
```

> 삭제는 `del_yn = 'Y'`로 처리하는 소프트 딜리트 방식입니다.

---

## API 명세

Base URL: `http://localhost:8080/api/board`

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/list?page={page}&pageSize={size}` | 게시글 목록 조회 (페이징) |
| GET | `/{id}` | 게시글 상세 조회 |
| POST | `/` | 게시글 등록 |
| PUT | `/{id}/update` | 게시글 수정 |
| PATCH | `/{id}/delete` | 게시글 삭제 (소프트 딜리트) |
| POST | `/{id}/password` | 비밀번호 확인 |

### 목록 조회 응답 예시

```json
{
  "list": [...],
  "page": 1,
  "totalPages": 3
}
```

### BoardDto 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 게시글 ID |
| title | String | 제목 |
| content | String | 내용 |
| author | String | 작성자 |
| password | String | 비밀번호 (4자리) |
| viewCount | int | 조회수 |
| regDate | LocalDateTime | 작성일 |
| updateDate | LocalDateTime | 수정일 |

---

## 환경 설정 및 실행

### Prerequisites

- Java 17
- PostgreSQL
- Gradle

### 데이터베이스 설정

1. PostgreSQL에서 `board` 데이터베이스 생성

```sql
CREATE DATABASE board;
```

2. 테이블 생성 및 샘플 데이터 삽입

```bash
psql -U {username} -d board -f sql/init.sql
```

3. `src/main/resources/application.yaml` 수정

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/board
    username: {your_username}
    password: {your_password}
```

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

실행 후 브라우저에서 `http://localhost:8080/index.html` 접속
