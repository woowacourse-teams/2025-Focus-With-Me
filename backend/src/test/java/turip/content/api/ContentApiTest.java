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
public class ContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM trip_course");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE trip_course ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/contents/{contentId} GET 컨텐츠 단건 조회 테스트")
    @Nested
    class ReadContentById {
        @DisplayName("contentId로 컨텐츠 단건 조회 성공 시 200 OK 코드와 컨텐츠 정보를 응답한다")
        @Test
        void readContentById1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            // when & then
            RestAssured.given().port(port)
                    .when().get("/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isFavorite", is(false));
        }

        @DisplayName("device-fid 헤더가 존재하며 찜이 되어 있는 경우 isFavorite이 true로 응답된다")
        @Test
        void readContentById_withDeviceFidHeader() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");
            jdbcTemplate.update(
                    "INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");
            jdbcTemplate.update(
                    "INSERT INTO favorite (member_id, content_id) VALUES (?, ?)", 1, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isFavorite", is(true));
        }

        @DisplayName("id에 해당하는 컨텐츠가 없는 경우 404 NOT FOUND 코드를 응답한다")
        @Test
        void readContentById2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO Creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO Content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            // when & then
            RestAssured.given().port(port)
                    .when().get("/contents/{id}", 20)
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/contents/keyword/count GET 키워드로 검색된 컨텐츠 수 조회 테스트")
    @Nested
    class ReadCountByKeyword {

        @DisplayName("키워드에 대한 컨텐츠 수 조회 성공 시 200 OK 코드와 컨텐츠 수를 응답한다")
        @Test
        void readCountByKeyword1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '여행하는 뭉치')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천 with 메이', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("keyword", "메이")
                    .when().get("/contents/keyword/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(1));
        }
    }

    @DisplayName("/contents/keyword GET 키워드 기반 컨텐츠 검색 테스트")
    @Nested
    class ReadByKeyword {

        @DisplayName("키워드 기반 검색 성공 시, 200 코드와 컨텐츠 정보들과 더 받아올 정보가 있는지 여부를 응답한다")
        @Test
        void readByKeyword_unloadable() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '메이')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("keyword", "메이")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2))
                    .body("loadable", is(false));
        }

        @DisplayName("키워드 기반 검색 성공 시, 200 코드와 컨텐츠 정보들과 더 받아올 정보가 있는지 여부를 응답한다")
        @Test
        void readByKeyword_loadable() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '메이')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("keyword", "메이")
                    .queryParam("size", 1)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1))
                    .body("loadable", is(true));
        }
    }

    @DisplayName("/contents/popular/favorites GET 주간 인기 컨텐츠 조회 테스트")
    @Nested
    class ReadWeeklyPopularFavoriteContents {

        @DisplayName("device-fid 헤더가 존재하지 않는 경우 컨텐츠 목록과 찜 상태 false를 응답한다. 성공 시 200 OK 코드를 응답한다")
        @Test
        void getPopularContentsWithoutDeviceFid() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', '여행채널')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 1, null)");
            jdbcTemplate.update("INSERT INTO content (creator_id, city_id, url, title, uploaded_date) " +
                    "VALUES (1, 1, 'https://youtube.com/watch?v=test', '서울 여행', '2025-07-28')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite (member_id, content_id, created_at) VALUES (1, 1, CURRENT_DATE - 7)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("size", 5)
                    .when().log().all().get("/contents/popular/favorites")
                    .then().log().all()
                    .statusCode(200)
                    .body("contents[0].content.id", is(1))
                    .body("contents[0].content.title", is("서울 여행"))
                    .body("contents[0].content.city.name", is("서울"))
                    .body("contents[0].content.isFavorite", is(false));
        }

        @DisplayName("device-fid 헤더가 존재하면 컨텐츠 목록과 찜 여부를 응답한다. 성공 시 200 OK 코드를 응답한다")
        @Test
        void getPopularContentsWithDeviceFid() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', '여행채널')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 1, null)");
            jdbcTemplate.update("INSERT INTO content (creator_id, city_id, url, title, uploaded_date) " +
                    "VALUES (1, 1, 'https://youtube.com/watch?v=test', '서울 여행', '2025-07-28')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite (member_id, content_id, created_at) VALUES (1, 1, CURRENT_DATE - 7)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("size", 5)
                    .when().get("/contents/popular/favorites")
                    .then()
                    .statusCode(200)
                    .body("contents[0].content.id", is(1))
                    .body("contents[0].content.title", is("서울 여행"))
                    .body("contents[0].content.city.name", is("서울"))
                    .body("contents[0].content.isFavorite", is(true));
        }
    }
}
