-- 초기 테이블 생성

-- 국가 테이블
CREATE TABLE IF NOT EXISTS country
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(255) NOT NULL
);

-- 주 테이블
CREATE TABLE IF NOT EXISTS province
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 도시 테이블
CREATE TABLE IF NOT EXISTS city
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    country_id  BIGINT       NOT NULL,
    province_id BIGINT       NULL, -- 해외는 주를 표기하지 않는다.
    name        VARCHAR(255) NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    CONSTRAINT fk_city_country
        FOREIGN KEY (country_id) REFERENCES country (id),
    CONSTRAINT fk_city_province
        FOREIGN KEY (province_id) REFERENCES province (id)
);

-- 크리에이터 테이블
CREATE TABLE IF NOT EXISTS creator
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_name  VARCHAR(255) NOT NULL UNIQUE,
    profile_image VARCHAR(255) NOT NULL
);

-- 카테고리 테이블
CREATE TABLE IF NOT EXISTS category
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


-- 멤버 테이블
CREATE TABLE IF NOT EXISTS member
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_fid  VARCHAR(255) NOT NULL UNIQUE,

    created_at  DATETIME(6)  NOT NULL,
    modified_at DATETIME(6)  NULL, -- 수정일은 null 가능
    deleted_at  DATETIME(6)  NULL  -- soft delete
);

-- 컨텐츠 테이블
CREATE TABLE IF NOT EXISTS content
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id    BIGINT       NOT NULL,
    city_id       BIGINT       NOT NULL,
    title         VARCHAR(255) NOT NULL,
    url           VARCHAR(255) NOT NULL,
    uploaded_date DATE         NOT NULL,

    CONSTRAINT fk_content_creator
        FOREIGN KEY (creator_id) REFERENCES creator (id),
    CONSTRAINT fk_content_city
        FOREIGN KEY (city_id) REFERENCES city (id),

    CONSTRAINT uq_content_creator_title
        UNIQUE (creator_id, title)
);

-- 장소 테이블
CREATE TABLE IF NOT EXISTS place
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    url       VARCHAR(500) NOT NULL UNIQUE,
    address   VARCHAR(255) NOT NULL,

    latitude  DOUBLE       NOT NULL,
    longitude DOUBLE       NOT NULL
);

-- 장소 카테고리 테이블
CREATE TABLE IF NOT EXISTS place_category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_id    BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_place_category__place
        FOREIGN KEY (place_id) REFERENCES place (id),
    CONSTRAINT fk_place_category__category
        FOREIGN KEY (category_id) REFERENCES category (id),

    -- 같은 장소-카테고리 조합 중복 방지
    CONSTRAINT uq_place_category__place_id_category_id
        UNIQUE (place_id, category_id)
);

-- 컨텐츠 플레이스 테이블
CREATE TABLE IF NOT EXISTS content_place
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    visit_day   INT    NOT NULL,
    visit_order INT    NOT NULL,
    time_line   TIME   NOT NULL,

    place_id    BIGINT NOT NULL,
    content_id  BIGINT NOT NULL,

    CONSTRAINT fk_content_place__place
        FOREIGN KEY (place_id) REFERENCES place (id),
    CONSTRAINT fk_content_place__content
        FOREIGN KEY (content_id) REFERENCES content (id),

    -- 같은 콘텐츠의 같은 일차에서 방문 순서 중복 방지
    CONSTRAINT uq_content_place__content_day_order
        UNIQUE (content_id, visit_day, visit_order)
);

-- 찜 폴더 테이블
CREATE TABLE IF NOT EXISTS favorite_folder
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    is_default  BOOLEAN      NOT NULL, -- 기본 폴더 여부

    created_at  DATETIME(6)  NOT NULL,
    modified_at DATETIME(6)  NULL,     -- 수정일은 null 가능
    deleted_at  DATETIME(6)  NULL,     -- soft delete

    CONSTRAINT fk_favorite_folder__member
        FOREIGN KEY (member_id) REFERENCES member (id),

    -- 사용자별 폴더 이름 중복 방지
    CONSTRAINT uq_favorite_folder__member_name UNIQUE (member_id, name)
);

-- 찜 장소 테이블
CREATE TABLE IF NOT EXISTS favorite_place
(
    id                 BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    favorite_folder_id BIGINT      NOT NULL,
    place_id           BIGINT      NOT NULL,

    created_at         DATETIME(6) NOT NULL,
    modified_at        DATETIME(6) NULL, -- 수정일은 null 가능
    deleted_at         DATETIME(6) NULL, -- soft delete

    CONSTRAINT fk_favorite_place__folder
        FOREIGN KEY (favorite_folder_id) REFERENCES favorite_folder (id),
    CONSTRAINT fk_favorite_place__place
        FOREIGN KEY (place_id) REFERENCES place (id),

    -- 같은 폴더 내 동일 장소 중복 방지
    CONSTRAINT uq_favorite_place__folder_place
        UNIQUE (favorite_folder_id, place_id)
);

-- 찜 컨텐츠 테이블
CREATE TABLE IF NOT EXISTS favorite_content
(
    id          BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT      NOT NULL,
    content_id  BIGINT      NOT NULL,

    created_at  DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NULL, -- 수정일은 null 가능
    deleted_at  DATETIME(6) NULL, -- soft delete

    CONSTRAINT fk_favorite_content__member
        FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_favorite_content__content
        FOREIGN KEY (content_id) REFERENCES content (id),

    -- 회원-컨텐츠 중복 방지
    CONSTRAINT uq_favorite_content__member_content
        UNIQUE (member_id, content_id)
);
