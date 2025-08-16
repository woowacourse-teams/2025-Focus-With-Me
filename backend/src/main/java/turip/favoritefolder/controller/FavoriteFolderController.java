package turip.favoritefolder.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.exception.ErrorResponse;
import turip.favoritefolder.controller.dto.request.FavoriteFolderNameRequest;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favoritefolder.service.FavoriteFolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-folders")
@Tag(name = "FavoriteFolder", description = "장소 찜 폴더 API")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    @Operation(
            summary = "장소 찜 폴더 생성 api",
            description = "장소 찜 폴더를 생성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 1,
                                                "name": "뭉치가 가고싶은 맛집들",
                                                "isDefault": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "folder_name_blank",
                                            summary = "장소 찜 폴더 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "message": "장소 찜 폴더 이름은 빈 칸이 될 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_name_length_over",
                                            summary = "장소 찜 폴더 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "message": "장소 찜 폴더 이름은 최대 20글자 입니다."
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
                                            name = "already_exists_folder_name",
                                            summary = "같은 이름의 폴더가 이미 존재하는 경우",
                                            value = """
                                                    {
                                                        "message": "중복된 폴더 이름이 존재합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoriteFolderResponse> create(@RequestHeader("device-fid") String deviceFid,
                                                         @RequestBody FavoriteFolderRequest request) {
        FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, deviceFid);
        return ResponseEntity.created(URI.create("/favorite-folders/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "장소 찜 폴더 조회 api",
            description = "특정 회원의 장소 찜 폴더 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersWithPlaceCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 조회 성공",
                                    value = """
                                            {
                                                "favoriteFolders": [
                                                    {
                                                        "id": 5,
                                                        "memberId": 8,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "placeCount": 0
                                                    },
                                                    {
                                                        "id": 6,
                                                        "memberId": 8,
                                                        "name": "뭉치가 가고싶은 맛집들",
                                                        "isDefault": false,
                                                        "placeCount": 0
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<FavoriteFoldersWithPlaceCountResponse> readAllByMember(
            @RequestHeader("device-fid") String deviceFid) {
        FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByDeviceFid(deviceFid);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 목록과 장소 찜 여부 조회 api",
            description = "특정 회원의 장소 찜 폴더 목록과, 특정 장소에 대한 찜 여부를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersWithFavoriteStatusResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 조회 성공",
                                    value = """
                                            {
                                                "favoriteFolders": [
                                                    {
                                                        "id": 5,
                                                        "memberId": 1,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "isFavoritePlace": true
                                                    },
                                                    {
                                                        "id": 6,
                                                        "memberId": 1,
                                                        "name": "잠실 맛집들",
                                                        "isDefault": false,
                                                        "isFavoritePlace": false
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "placeId에 대한 장소가 존재하지 않는 경우",
                                    value = """
                                            {
                                                "message": "해당 id에 대한 장소가 존재하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/favorite-status")
    public ResponseEntity<FavoriteFoldersWithFavoriteStatusResponse> readAllWithFavoriteStatusByDeviceId(
            @RequestHeader("device-fid") String deviceFid, @RequestParam("placeId") Long placeId) {
        FavoriteFoldersWithFavoriteStatusResponse response = favoriteFolderService.findAllWithFavoriteStatusByDeviceId(
                deviceFid, placeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 이름 수정 api",
            description = "장소 찜 폴더의 이름을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 이름 수정 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 1,
                                                "name": "수정된 폴더명",
                                                "isDefault": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "folder_name_blank",
                                            summary = "장소 찜 폴더 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "message": "장소 찜 폴더 이름은 빈 칸이 될 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_name_length_over",
                                            summary = "장소 찜 폴더 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "message": "장소 찜 폴더 이름은 최대 20글자 입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "is_default_folder",
                                            summary = "장소 찜 폴더가 기본 폴더인 경우",
                                            value = """
                                                    {
                                                        "message": "기본 폴더는 수정할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                            name = "member_not_found",
                                            summary = "device-fid에 대한 회원을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 회원이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "id에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 폴더가 존재하지 않습니다."
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
                                            name = "folder_name_already_exists",
                                            summary = "중복되는 폴더 이름이 존재하는 경우",
                                            value = """
                                                    {
                                                        "message" : "중복된 폴더 이름이 존재합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/{favoriteFolderId}")
    public ResponseEntity<FavoriteFolderResponse> updateName(
            @RequestHeader("device-fid") String deviceFid,
            @PathVariable Long favoriteFolderId,
            @RequestBody FavoriteFolderNameRequest request
    ) {
        FavoriteFolderResponse response = favoriteFolderService.updateName(deviceFid, favoriteFolderId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 삭제 api",
            description = "장소 찜 폴더를 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "is_default_folder",
                                    summary = "삭제하려는 폴더가 기본 폴더인 경우",
                                    value = """
                                            {
                                                "message" : "기본 폴더는 삭제할 수 없습니다."
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
                            examples = @ExampleObject(
                                    name = "not_folder_owner",
                                    summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                    value = """
                                            {
                                                "message" : "폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다."
                                            }
                                            """
                            )
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
                                            summary = "device-fid에 대한 회원을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 회원이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "id에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "message" : "해당 id에 대한 폴더가 존재하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping("/{favoriteFolderId}")
    public ResponseEntity<Void> delete(@RequestHeader("device-fid") String deviceFid,
                                       @PathVariable Long favoriteFolderId) {
        favoriteFolderService.remove(deviceFid, favoriteFolderId);
        return ResponseEntity.noContent().build();
    }
}
