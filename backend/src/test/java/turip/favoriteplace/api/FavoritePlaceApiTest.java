package turip.favoriteplace.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
public class FavoritePlaceApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/favorites-places POST 장소 찜 생성 테스트")
    @Nested
    class Create {

        @DisplayName("장소 찜 생성에 성공한 경우 201 CREATED 코드와 생성된 장소 찜 정보를 응답한다")
        @Test
        void create1() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-places")
                    .then()
                    .statusCode(201);
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 403 FORBIDDEN을 응답한다")
        @Test
        void create2() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('may')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "cool")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-places")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void create3() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void create4() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("이미 해당 폴더에 찜한 장소인 경우 409 CONFLICT를 응답한다")
        @Test
        void create5() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-places")
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("/favorites-places DELETE 장소 찜 삭제 테스트")
    @Nested
    class Delete {

        @DisplayName("장소 찜 삭제에 성공한 경우 204 NO CONTENT를 응답한다")
        @Test
        void delete1() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(204);
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 403 FORBIDDEN을 응답한다")
        @Test
        void delete2() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('may')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('cool')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");
            jdbcTemplate.update("INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "cool")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("deviceFid에 대한 회원이 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete3() {
            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "nonExistentDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete4() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 999L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete5() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 999L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("삭제하려는 장소 찜이 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void delete6() {
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '잠실캠 맛집 모음', false)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY', '루터회관의 도로명 주소', 38.1234, 127.23123)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("favoriteFolderId", 1L)
                    .queryParam("placeId", 1L)
                    .contentType(ContentType.JSON)
                    .when().delete("/favorite-places")
                    .then()
                    .statusCode(404);
        }
    }
}
