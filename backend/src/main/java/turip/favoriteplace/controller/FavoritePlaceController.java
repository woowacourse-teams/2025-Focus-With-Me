package turip.favoriteplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.exception.ErrorResponse;
import turip.favoriteplace.controller.dto.response.FavoritePlaceResponse;
import turip.favoriteplace.service.FavoritePlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-places")
@Tag(name = "FavoritePlace", description = "장소 찜 API")
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @Operation(
            summary = "장소 찜 생성 api",
            description = "장소를 찜한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePlaceResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "favoriteFolderId": 3,
                                                "placeId": 5
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_folder_owner",
                                            summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                            value = """
                                                    {
                                                        "message" : "폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "favoriteFolderId에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 폴더가 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 장소가 존재하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "already_favorite",
                                            summary = "이미 해당 폴더에 찜한 상태인 경우",
                                            value = """
                                                    {
                                                        "message": "이미 해당 폴더에 찜한 장소입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoritePlaceResponse> create(
            @RequestHeader("device-fid") String deviceFid,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        FavoritePlaceResponse response = favoritePlaceService.create(deviceFid, favoriteFolderId, placeId);
        return ResponseEntity.created(URI.create("/favorite-places/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "장소 찜 삭제 api",
            description = "장소 찜을 취소한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_folder_owner",
                                            summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                            value = """
                                                    {
                                                        "message" : "폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "member_not_found",
                                            summary = "deviceFid에 대한 회원을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 회원이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "favoriteFolderId에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 폴더가 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 장소가 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "favorite_place_not_found",
                                            summary = "해당 폴더에 장소 찜이 되어있지 않은 경우",
                                            value = """
                                                    {
                                                        "message" : "삭제하려는 장소 찜이 존재하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestHeader("device-fid") String deviceFid,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        favoritePlaceService.remove(deviceFid, favoriteFolderId, placeId);
        return ResponseEntity.noContent().build();
    }
}
