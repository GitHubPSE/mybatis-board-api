# Board

Spring Boot + MyBatis + PostgreSQL 기반의 게시판 CRUD 웹 애플리케이션입니다.

**Live Demo**: [https://boardforduck.duckdns.org](https://boardforduck.duckdns.org)

## 기술 스택

- **Backend**: Spring Boot 4.0.6, Java 17
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

## 프로젝트 구조

```
board/
├── .github/workflows/
│   └── ci.yml                    # 테스트 → 이미지 빌드/푸시 → 배포
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
│   │           ├── css/style.css
│   │           ├── index.html    # 게시글 목록
│   │           ├── detail.html   # 게시글 상세
│   │           └── edit.html     # 게시글 작성/수정
│   └── test/
│       ├── java/com/mybatis_crud/board/
│       │   ├── controller/BoardControllerTest.java   # MockMvc (HTTP 계층)
│       │   ├── service/BoardServiceTest.java          # Mockito (서비스 로직)
│       │   └── mapper/BoardMapperTest.java            # Testcontainers (SQL 검증)
│       └── resources/
│           └── schema.sql        # 테스트 DB 스키마 (샘플 데이터 없음)
├── sql/
│   ├── init.sql                  # 테이블 생성 및 샘플 데이터
│   └── ERD.png
├── docker-compose.yml             # db + app 실행 (로컬 / 운영 공통)
├── Dockerfile                     # 멀티스테이지 빌드 (build → JRE 런타임)
├── .env.example                   # 필요한 환경변수 템플릿
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

## 로컬 실행

### Prerequisites

- Java 17
- Docker
- Gradle

### Docker Compose로 실행 (권장)

```bash
docker compose up -d --build
```

DB + 앱이 한 번에 뜹니다. 최초 실행 시 `sql/init.sql`이 자동으로 실행되어 테이블과 샘플 데이터까지 준비됩니다. 기본값(`board`/`board`, `localhost:5432`)으로 동작하며, 다른 값을 쓰려면 `.env.example`을 참고해 `.env` 파일을 만들면 됩니다.

실행 후 `http://localhost:8080/index.html` 접속.

### Gradle로 앱만 직접 실행 (DB는 Docker 필요)

```bash
docker compose up -d db
./gradlew bootRun
```

---

## 테스트

세 계층으로 나눠서 검증합니다.

| 계층 | 파일 | 방식 | 검증 내용 |
|------|------|------|-----------|
| Controller | `BoardControllerTest` | MockMvc (`@WebMvcTest`) | HTTP 요청/응답 형식, 라우팅 |
| Service | `BoardServiceTest` | Mockito | 페이지네이션 계산, 비밀번호 검증 등 로직 |
| Mapper | `BoardMapperTest` | Testcontainers (`@MybatisTest`) | `BoardMapper.xml`의 실제 SQL이 PostgreSQL에서 의도대로 동작하는지 |

```bash
./gradlew test
```

`BoardMapperTest`는 실행 시 Docker로 PostgreSQL 컨테이너를 자동으로 띄워서 검증하므로 **Docker가 실행 중이어야** 합니다.

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
