package turip.regioncategory.api;

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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegionCategoryApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");

        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/regionCategories GET 지역 카테고리 조회 테스트")
    @Nested
    class getRegionCategories {

        @DisplayName("국내 지역 카테고리 조회 성공 시 200 OK 코드와 국내 지역 카테고리 목록을 응답한다")
        @Test
        void getRegionCategoriesByCountryType_withIsKoreaTrue_returnsDomesticCategories() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, 'https://example.com/busan.jpg')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", true)
                    .when().get("/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(3)) // 서울, 부산, 국내 기타
                    .body("regionCategories[0].regionCategoryName", is("서울"))
                    .body("regionCategories[1].regionCategoryName", is("부산"))
                    .body("regionCategories[2].regionCategoryName", is("국내 기타"));
        }

        @DisplayName("해외 지역 카테고리 조회 성공 시 200 OK 코드와 해외 지역 카테고리 목록을 응답한다")
        @Test
        void getRegionCategoriesByCountryType_withIsKoreaFalse_returnsOverseasCategories() {
            // given
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('일본', 'https://example.com/japan.jpg')");
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('중국', 'https://example.com/china.jpg')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", false)
                    .when().get("/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(3)) // 일본, 중국, 해외 기타
                    .body("regionCategories[0].regionCategoryName", is("일본"))
                    .body("regionCategories[1].regionCategoryName", is("중국"))
                    .body("regionCategories[2].regionCategoryName", is("해외 기타"));
        }
    }
} 
