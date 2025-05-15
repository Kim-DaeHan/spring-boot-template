# 도서 관리 시스템 API

도서관리 시스템을 위한 RESTful API 서비스입니다. Spring Boot, JPA, SQLite3를 사용하여 개발되었으며, Swagger UI를 통해 API 문서를 제공합니다.

## 기술 스택

- Spring Boot 3.4.5
- Spring Data JPA
- SQLite3
- Swagger UI (springdoc-openapi)
- Lombok
- JUnit 5 (테스트)

## 주요 기능

- 도서 관리
  - 도서 등록, 조회, 검색, 상태 변경
  - 카테고리별 도서 분류
  - 다중 카테고리 지원
- 카테고리 관리
  - 카테고리 등록, 조회
  - 카테고리별 도서 조회
- 대여 관리
  - 도서 대여 및 반납 처리
  - 대여 기한 관리
  - 연체 도서 자동 상태 변경
  - 연체 도서 조회

## API 엔드포인트

### 도서 관련 API

- `POST /api/books` - 도서 등록
- `GET /api/books` - 도서 목록 조회
- `GET /api/books/{id}` - 도서 상세 조회
- `GET /api/books/search` - 도서 검색
- `PATCH /api/books/{id}/status` - 도서 상태 변경
- `PUT /api/books/{id}/categories` - 도서 카테고리 수정

### 카테고리 관련 API

- `POST /api/categories` - 카테고리 등록
- `GET /api/categories` - 카테고리 목록 조회
- `GET /api/categories/{id}` - 카테고리 조회
- `GET /api/categories/{id}/books` - 카테고리별 도서 조회

### 대여 관련 API

- `POST /api/rentals/borrow` - 도서 대여
- `PUT /api/rentals/{id}/return` - 도서 반납
- `GET /api/rentals` - 대여 목록 조회
- `GET /api/rentals/{id}` - 대여 상세 조회
- `GET /api/rentals/overdue` - 연체 목록 조회

## 실행 방법

### 필수 조건

- Java 17 이상
- Gradle 7.x 이상

### 로컬 실행

```bash
# 프로젝트 클론
git clone https://github.com/yourusername/library-api.git
cd library-api

# 애플리케이션 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## API 문서 접근 방법

Swagger UI를 통해 API 문서 및 테스트 인터페이스에 접근할 수 있습니다.

```
http://localhost:8080/swagger-ui
```

API 명세는 다음 주소에서 JSON 형식으로 확인할 수 있습니다.

```
http://localhost:8080/api-docs
```

## 데이터베이스

SQLite3 데이터베이스를 사용합니다. 애플리케이션 실행 시 `library.db` 파일이 루트 디렉토리에 생성되며, 초기 스키마와 데이터는 다음 파일들을 통해 자동으로 로드됩니다:

- `src/main/resources/schema.sql`: 테이블 구조 정의
- `src/main/resources/data.sql`: 초기 샘플 데이터

### 데이터 모델

애플리케이션은 다음과 같은 테이블 구조를 사용합니다:

- `books`: 도서 정보
- `categories`: 카테고리 정보
- `book_categories`: 도서-카테고리 다대다 관계
- `rentals`: 대여 정보

## 엔티티 구조

- **Book**: 도서 정보

  - id: 고유 식별자 (자동 증가)
  - title: 제목 (NOT NULL)
  - author: 지은이 (NOT NULL)
  - status: 상태 (AVAILABLE, UNAVAILABLE)
  - categories: 카테고리 목록 (다대다 관계)
  - createdAt: 생성일
  - updatedAt: 수정일

- **Category**: 카테고리 정보

  - id: 고유 식별자 (자동 증가)
  - name: 카테고리명 (NOT NULL, UNIQUE)
  - books: 해당 카테고리에 속한 도서 목록 (다대다 관계)

- **Rental**: 대여 정보
  - id: 고유 식별자 (자동 증가)
  - book: 대여한 도서 (다대일 관계)
  - dueDate: 반납 예정일 (NOT NULL)
  - returnedDate: 실제 반납일
  - status: 상태 (BORROWED, RETURNED, OVERDUE)
  - createdAt: 생성일
  - updatedAt: 수정일

## 예외 처리 구조

이 프로젝트는 계층적인 예외 처리 구조를 사용합니다:

### 예외 클래스

- `BusinessException`: 모든 비즈니스 로직 예외의 기본 클래스
  - `ResourceNotFoundException`: 요청한 리소스를 찾을 수 없을 때 발생 (HTTP 404)
  - `InvalidRequestException`: 잘못된 요청 형식이나 값을 제공했을 때 발생 (HTTP 400)
  - `DuplicateResourceException`: 이미 존재하는 리소스를 생성하려 할 때 발생 (HTTP 409)
  - `ResourceInUseException`: 사용 중인 리소스를 조작하려 할 때 발생 (HTTP 409)

### 글로벌 예외 처리

`GlobalExceptionHandler`는 다음과 같은 예외 유형을 처리합니다:

1. `BusinessException`: 모든 비즈니스 예외를 적절한 HTTP 상태 코드와 함께 처리
2. `MethodArgumentNotValidException`: 유효성 검증 오류 처리
3. `HttpMessageNotReadableException`: 요청 바디 파싱 오류 (특히 Enum 값 오류)
4. `RuntimeException`: 기타 런타임 예외 처리
5. `Exception`: 예상치 못한 일반 예외 처리

### 응답 형식

모든 오류는 다음 형식의 JSON으로 응답합니다:

```json
{
  "status": 400,
  "message": "오류 메시지",
  "timestamp": "2023-05-01T12:34:56",
  "errors": {
    // 필드 오류가 있는 경우에만 포함
    "필드명": "오류 메시지"
  }
}
```

## 설정 정보

애플리케이션 설정은 `application.yml` 파일에서 관리됩니다:

```yaml
spring:
  datasource:
    url: jdbc:sqlite:./library.db
    driver-class-name: org.sqlite.JDBC

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.community.dialect.SQLiteDialect

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui
  api-docs:
    path: /api-docs
```

## 사용법 예시

### 도서 목록 조회

```bash
curl -X GET http://localhost:8080/api/books
```

### 카테고리별 도서 조회

```bash
curl -X GET http://localhost:8080/api/categories/1/books
```

### 도서 대여하기

```bash
curl -X POST http://localhost:8080/api/rentals/borrow \
  -H "Content-Type: application/json" \
  -d '{"bookId": 1, "dueDate": "2023-12-31"}'
```

### 도서 반납하기

```bash
curl -X PUT http://localhost:8080/api/rentals/1/return
```
