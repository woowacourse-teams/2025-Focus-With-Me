package turip.content.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContentCountApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM trip_course");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");

        jdbcTemplate.update("ALTER TABLE trip_course ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/contents/count GET 지역 카테고리별 컨텐츠 수 조회 테스트")
    @Nested
    class getCountByRegionCategory {

        @DisplayName("국내 특정 도시(서울) 조회 성공 시 200 OK 코드와 해당 도시의 컨텐츠 수를 응답한다")
        @Test
        void getCountByRegionCategory_withSeoul_returnsSeoulContentCount() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('부산', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd2', '서울 맛집 투어', '2024-07-02')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, 'https://youtube.com/watch?v=abcd3', '부산 여행 가이드', '2024-07-03')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "서울")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2));
        }

        @DisplayName("해외 특정 국가(일본) 조회 성공 시 200 OK 코드와 해당 국가의 컨텐츠 수를 응답한다")
        @Test
        void getCountByRegionCategory_withJapan_returnsJapanContentCount() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('일본')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('중국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('도쿄', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('오사카', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('베이징', 2)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '도쿄 여행 가이드', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, 'https://youtube.com/watch?v=abcd2', '오사카 맛집 투어', '2024-07-02')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 3, 'https://youtube.com/watch?v=abcd3', '베이징 관광', '2024-07-03')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "일본")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2));
        }

        @DisplayName("국내 기타 조회 성공 시 200 OK 코드와 DomesticRegionCategory에 없는 도시들의 컨텐츠 수를 응답한다")
        @Test
        void getCountByRegionCategory_withDomesticEtc_returnsOtherDomesticContentCount() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('대구', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('울산', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 여행', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, 'https://youtube.com/watch?v=abcd2', '대구 여행', '2024-07-02')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 3, 'https://youtube.com/watch?v=abcd3', '울산 여행', '2024-07-03')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "국내 기타")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2)); // 대구, 울산
        }

        @DisplayName("해외 기타 조회 성공 시 200 OK 코드와 OverseasRegionCategory에 없는 국가들의 컨텐츠 수를 응답한다")
        @Test
        void getCountByRegionCategory_withOverseasEtc_returnsOtherOverseasContentCount() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('일본')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('태국')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('싱가포르')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('도쿄', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('방콕', 2)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('싱가포르시티', 3)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '도쿄 여행', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, 'https://youtube.com/watch?v=abcd2', '방콕 여행', '2024-07-02')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 3, 'https://youtube.com/watch?v=abcd3', '싱가포르 여행', '2024-07-03')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "해외 기타")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2)); // 태국, 싱가포르
        }

        @DisplayName("존재하지 않는 카테고리로 탐색하는 경우 400 Bad Request 코드를 응답한다")
        @Test
        void getCountByRegionCategory_withInvalidCategory_returnsBadRequest() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('일본')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('태국')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('싱가포르')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('도쿄', 1)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('방콕', 2)");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('싱가포르시티', 3)");
            
            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "아라키스")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(400);
        }
    }
} 
