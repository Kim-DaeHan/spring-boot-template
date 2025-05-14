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

- 도서 등록, 조회, 검색, 상태 변경
- 카테고리 등록, 조회
- 카테고리별 도서 조회
- 도서 대여 및 반납
- 연체 도서 관리

## 프로젝트 구조

프로젝트는 모듈별로 구성되어 있으며, 각 모듈 내에 관련 기능들을 그룹화하였습니다:

```
src/main/java/com/example/libraryapi
├── book/               # 도서 관련 모듈
│   ├── controller/     # 도서 컨트롤러
│   ├── dto/            # 도서 DTO (record 클래스)
│   ├── entity/         # 도서 엔티티
│   ├── mapper/         # 도서 매퍼
│   ├── repository/     # 도서 리포지토리
│   └── service/        # 도서 서비스
│
├── category/           # 카테고리 관련 모듈
│   ├── controller/     # 카테고리 컨트롤러
│   ├── dto/            # 카테고리 DTO (record 클래스)
│   ├── entity/         # 카테고리 엔티티
│   ├── mapper/         # 카테고리 매퍼
│   ├── repository/     # 카테고리 리포지토리
│   └── service/        # 카테고리 서비스
│
├── rental/             # 대여 관련 모듈
│   ├── controller/     # 대여 컨트롤러
│   ├── dto/            # 대여 DTO (record 클래스)
│   ├── entity/         # 대여 엔티티
│   ├── mapper/         # 대여 매퍼
│   ├── repository/     # 대여 리포지토리
│   └── service/        # 대여 서비스
│
├── config/             # 전역 설정
├── exception/          # 전역 예외 처리
└── LibraryApiApplication.java  # 애플리케이션 시작점
```

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

```bash
./gradlew bootRun
```

## API 문서 접근 방법

```
http://localhost:8080/api/swagger-ui.html
```

## 데이터베이스

SQLite3 데이터베이스를 사용합니다. 애플리케이션 실행 시 `library.db` 파일이 생성되며, 초기 데이터는 `data.sql` 파일을 통해 자동으로 로드됩니다.

## 엔티티 구조

- **Book**: 도서 정보

  - id: 고유 식별자
  - title: 제목
  - author: 지은이
  - status: 상태 (AVAILABLE, UNAVAILABLE)
  - categories: 카테고리 목록
  - createdAt: 생성일
  - updatedAt: 수정일

- **Category**: 카테고리 정보

  - id: 고유 식별자
  - name: 카테고리명
  - books: 해당 카테고리에 속한 도서 목록

- **Rental**: 대여 정보
  - id: 고유 식별자
  - book: 대여한 도서
  - dueDate: 반납 예정일
  - returnedDate: 실제 반납일
  - status: 상태 (BORROWED, RETURNED, OVERDUE)
  - createdAt: 생성일
  - updatedAt: 수정일
