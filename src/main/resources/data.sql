-- 카테고리 등록
INSERT INTO categories (name) VALUES ('문학') ON CONFLICT(name) DO NOTHING;
INSERT INTO categories (name) VALUES ('경제경영') ON CONFLICT(name) DO NOTHING;
INSERT INTO categories (name) VALUES ('IT') ON CONFLICT(name) DO NOTHING;
INSERT INTO categories (name) VALUES ('인문학') ON CONFLICT(name) DO NOTHING;
INSERT INTO categories (name) VALUES ('과학') ON CONFLICT(name) DO NOTHING;

-- 도서 등록
-- 문학 카테고리 도서
INSERT INTO books (title, author, status) VALUES ('너에게 해주지 못한 말들', '권태영', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (1, 1);

INSERT INTO books (title, author, status) VALUES ('단순하게 배부르게', '현영서', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (2, 1);

INSERT INTO books (title, author, status) VALUES ('게으른 사랑', '권태영', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (3, 1);

-- 경제경영 카테고리 도서
INSERT INTO books (title, author, status) VALUES ('트랜드 코리아 2322', '권태영', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (4, 2);

INSERT INTO books (title, author, status) VALUES ('초격자 투자', '장동혁', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (5, 2);

INSERT INTO books (title, author, status) VALUES ('파이어족 강환국의 하면 되지 않는다! 퀀트 투자', '홍길동', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (6, 2);

-- 인문학 카테고리 도서
INSERT INTO books (title, author, status) VALUES ('진심보다 밥', '이서연', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (7, 4);

INSERT INTO books (title, author, status) VALUES ('실패에 대하여 생각하지 마라', '위성원', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (8, 4);

-- IT 카테고리 도서
INSERT INTO books (title, author, status) VALUES ('실리콘밸리 리더십 쉽다', '지승열', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (9, 3);

INSERT INTO books (title, author, status) VALUES ('데이터분석을 위한 A 프로그래밍', '지승열', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (10, 3);

INSERT INTO books (title, author, status) VALUES ('인공지능1-12', '장동혁', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (11, 3);

INSERT INTO books (title, author, status) VALUES ('-1년차 게임 개발', '위성원', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (12, 3);

INSERT INTO books (title, author, status) VALUES ('Skye가 알려주는 피부 채색의 비결', '권태영', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (13, 3);

-- 과학 카테고리 도서
INSERT INTO books (title, author, status) VALUES ('자연의 발전', '장지명', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (14, 5);

INSERT INTO books (title, author, status) VALUES ('코스모스 필 무렵', '이승열', 'AVAILABLE');
INSERT INTO book_categories (book_id, category_id) VALUES (15, 5); 