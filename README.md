# Board

Spring Boot + MyBatis + PostgreSQL 기반의 영화 리뷰 게시판 웹 애플리케이션입니다. JWT 기반 회원가입/로그인과 작성자 본인만 수정/삭제할 수 있는 권한 체크를 포함합니다.

**Live Demo**: [https://boardforduck.duckdns.org](https://boardforduck.duckdns.org)

## 기술 스택

- **Backend**: Spring Boot 4.0.6, Java 17
- **인증**: Spring Security, JWT (JJWT)
- **ORM**: MyBatis 4.0.1
- **Database**: PostgreSQL
- **Frontend**: HTML, jQuery 3.7.0, Bootstrap 5.3.0
- **Build Tool**: Gradle
- **Test**: JUnit5, Mockito, MockMvc, Testcontainers
- **Monitoring**: Spring Boot Actuator (`/actuator/health`)
- **Infra/CI-CD**: Docker, GitHub Actions, GitHub Container Registry(ghcr.io), AWS Lightsail, Nginx, Let's Encrypt

---

## 배포 아키텍처

```
GitHub Actions
  ├─ test            ./gradlew test (Mockito / MockMvc / Testcontainers)
  ├─ build-and-push   Docker 이미지 빌드 → ghcr.io 업로드 (테스트 통과 시에만)
  └─ deploy           ghcr.io에서 이미지 pull → AWS Lightsail에서 실행

AWS Lightsail (1대)
  Nginx(리버스 프록시, HTTPS) → Docker Compose
                                  ├─ app  (Spring Boot, ghcr.io 이미지 pull)
                                  └─ db   (PostgreSQL)
```

빌드는 GitHub Actions에서 처리하고, Lightsail은 완성된 이미지를 받아 실행만 합니다. `main`에 push되면 테스트 → 이미지 빌드 → 배포가 자동으로 이어집니다.

---

## 인증 흐름 (JWT)

```
회원가입  POST /api/auth/signup
  → 비밀번호를 BCrypt로 해시해서 저장

로그인   POST /api/auth/login
  → AuthenticationManager가 아이디/비밀번호를 검증 (CustomUserDetailsService + PasswordEncoder)
  → 성공하면 JWT 발급 ({ "token": "..." })

이후 요청  Authorization: Bearer <token> 헤더로 토큰 전달
  → JwtAuthenticationFilter가 매 요청마다 토큰을 검증해 SecurityContext에 인증 정보를 채움
  → SecurityConfig의 인가 규칙에 따라 통과/거부 결정
```

- 게시글 **조회(GET)**는 비로그인 상태에서도 가능합니다.
- 게시글 **작성/수정/삭제**는 로그인이 필요하고, 수정/삭제는 **작성자 본인**만 가능합니다(다른 사용자가 시도하면 403).
- 토큰은 서버에 저장하지 않습니다(stateless). 클라이언트(`localStorage`)가 들고 있다가 매 요청에 첨부합니다.

---

## 프로젝트 구조

```
board/
├── .github/workflows/
│   └── ci.yml                    # 테스트 → 이미지 빌드/푸시 → 배포
├── src/
│   ├── main/
│   │   ├── java/com/mybatis_crud/board/
│   │   │   ├── BoardApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java       # 인가 규칙, 필터 체인, PasswordEncoder/AuthenticationManager 빈
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java              # 토큰 발급/검증
│   │   │   │   ├── JwtAuthenticationFilter.java  # 매 요청 토큰 검사
│   │   │   │   └── CustomUserDetailsService.java # 로그인 아이디로 사용자 조회
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       # 회원가입/로그인
│   │   │   │   └── BoardController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   └── BoardService.java         # 작성자 본인 확인(소유권 체크) 포함
│   │   │   ├── mapper/
│   │   │   │   ├── UserMapper.java
│   │   │   │   └── BoardMapper.java
│   │   │   └── dto/
│   │   │       ├── AuthRequest.java
│   │   │       ├── UserDto.java
│   │   │       └── BoardDto.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── mapper/
│   │       │   ├── UserMapper.xml
│   │       │   └── BoardMapper.xml
│   │       └── static/
│   │           ├── css/style.css
│   │           ├── js/auth.js        # 토큰 저장/조회, 로그인 상태 표시 공통 로직
│   │           ├── login.html
│   │           ├── signup.html
│   │           ├── index.html    # 게시글 목록
│   │           ├── detail.html   # 게시글 상세 (작성자 본인만 수정/삭제 버튼 노출)
│   │           └── edit.html     # 게시글 작성/수정 (로그인 필요)
│   └── test/
│       ├── java/com/mybatis_crud/board/
│       │   ├── controller/
│       │   │   ├── AuthControllerTest.java
│       │   │   └── BoardControllerTest.java   # MockMvc (HTTP 계층)
│       │   ├── service/
│       │   │   ├── AuthServiceTest.java
│       │   │   └── BoardServiceTest.java      # Mockito (서비스 로직, 소유권 체크 포함)
│       │   └── mapper/
│       │       ├── UserMapperTest.java
│       │       └── BoardMapperTest.java       # Testcontainers (SQL 검증)
│       └── resources/
│           └── schema.sql        # 테스트 DB 스키마 (샘플 데이터 없음)
├── sql/
│   └── init.sql                  # 테이블 생성 및 샘플 데이터
├── docker-compose.yml             # db + app 실행 (로컬 / 운영 공통)
├── Dockerfile                     # 멀티스테이지 빌드 (build → JRE 런타임)
├── .env.example                   # 필요한 환경변수 템플릿
├── build.gradle
└── settings.gradle
```

---

## DB 스키마

```sql
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
```

> 삭제는 `del_yn = 'Y'`로 처리하는 소프트 딜리트 방식입니다. 게시글의 작성자는 `user_id`(로그인 아이디)로 식별하며, 화면에는 이 값이 그대로 작성자로 표시됩니다.

---

## API 명세

Base URL: `http://localhost:8080`

### 인증

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인, 성공 시 `{ "token": "..." }` 반환 | X |

### 게시판

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/api/board/list?page={page}&pageSize={size}[&keyword={keyword}&searchType={title\|userId}]` | 게시글 목록 조회 (페이징, 제목/작성자 검색) | X |
| GET | `/api/board/{id}` | 게시글 상세 조회 | X |
| POST | `/api/board` | 게시글 등록 | O |
| PUT | `/api/board/{id}/update` | 게시글 수정 (작성자만) | O |
| PATCH | `/api/board/{id}/delete` | 게시글 삭제 (소프트 딜리트, 작성자만) | O |

인증이 필요한 요청은 `Authorization: Bearer <token>` 헤더가 있어야 하며, 작성자가 아닌 사용자가 수정/삭제를 시도하면 403을 반환합니다.

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
| userId | String | 작성자 (로그인 아이디) |
| viewCount | int | 조회수 |
| regDate | LocalDateTime | 작성일 |
| updateDate | LocalDateTime | 수정일 |

---

## 로컬 실행

### Prerequisites

- Java 17
- Docker
- Gradle

### 환경변수 설정

`.env.example`을 참고해 `.env` 파일을 만들어주세요. 특히 `JWT_SECRET`은 비워두면 앱이 기동하지 않습니다(의도적인 fail-fast).

```bash
cp .env.example .env
# JWT_SECRET을 실제 랜덤 값으로 교체
openssl rand -base64 48
```

### Docker Compose로 실행 (권장)

```bash
docker compose up -d --build
```

DB + 앱이 한 번에 뜹니다. 최초 실행 시 `sql/init.sql`이 자동으로 실행되어 테이블과 샘플 데이터(샘플 계정 `user1`/`user2`/`user3`, 비밀번호 전부 `1234`)까지 준비됩니다.

실행 후 `http://localhost:8080/index.html` 접속.

> DB 비밀번호 등 `.env` 값을 바꿨는데 반영이 안 되면, 기존 볼륨에 예전 값으로 이미 초기화돼 있어서 그렇습니다. `docker compose down -v`로 볼륨까지 지우고 다시 올려주세요.

### Gradle로 앱만 직접 실행 (DB는 Docker 필요)

`build.gradle`의 `bootRun` 태스크가 `.env`를 자동으로 읽어 환경변수를 주입합니다.

로컬에서 DB 포트를 노출하려면 `docker-compose.override.yml`을 생성합니다 (`.gitignore` 처리되어 커밋되지 않습니다):

```yaml
services:
  db:
    ports:
      - "5432:5432"
```

```bash
docker compose up -d db
./gradlew bootRun
```

---

## 테스트

네 계층으로 나눠서 검증합니다.

| 계층 | 파일 | 방식 | 검증 내용 |
|------|------|------|-----------|
| Controller | `BoardControllerTest`, `AuthControllerTest` | MockMvc (`@WebMvcTest`) | HTTP 요청/응답 형식, 라우팅, 인증된 사용자로 동작하는지(`@WithMockUser`) |
| Service | `BoardServiceTest`, `AuthServiceTest` | Mockito | 페이지네이션 계산, 회원가입/로그인 로직, 작성자 본인 확인(소유권 체크) |
| Mapper | `BoardMapperTest`, `UserMapperTest` | Testcontainers (`@MybatisTest`) | 매퍼 XML의 실제 SQL이 PostgreSQL에서 의도대로 동작하는지 |

```bash
./gradlew test
```

`*MapperTest`는 실행 시 Docker로 PostgreSQL 컨테이너를 자동으로 띄워서 검증하므로 **Docker가 실행 중이어야** 합니다.

---

## 헬스체크

Spring Boot Actuator를 통해 애플리케이션과 DB 연결 상태를 확인할 수 있습니다.

```bash
curl http://localhost:8080/actuator/health
```

```json
{"status":"UP"}
```

서버 인스턴스가 켜져 있는 것과 애플리케이션이 정상적으로 요청을 처리할 수 있는 것은 다른 문제이기 때문에, 배포 환경에서 이 엔드포인트로 실제 동작 여부를 확인합니다.

---

## CI/CD

`main` 브랜치에 push되면 `.github/workflows/ci.yml`이 아래 순서로 실행됩니다.

1. **test** — `./gradlew test` (실패 시 이후 단계 전부 중단)
2. **build-and-push** — Docker 이미지를 빌드해 `ghcr.io/githubpse/mybatis-board-api-app`에 업로드
3. **deploy** — SSH로 AWS Lightsail에 접속해 `docker compose pull` + `docker compose up -d`로 새 이미지 반영

이미지 빌드는 GitHub Actions의 러너에서 처리하고, Lightsail은 완성된 이미지를 받아 실행만 하기 때문에 운영 서버에 별도의 빌드 부담이 없습니다.

> 운영 환경의 `JWT_SECRET`은 Lightsail에 별도로 설정된 환경변수를 사용하며, 저장소에는 포함되지 않습니다.
