package turip.favoriteplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.exception.custom.ConflictException;
import turip.exception.custom.ForbiddenException;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.controller.dto.response.FavoritePlaceResponse;
import turip.favoriteplace.domain.FavoritePlace;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@ExtendWith(MockitoExtension.class)
class FavoritePlaceServiceTest {

    @InjectMocks
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @Mock
    private PlaceRepository placeRepository;

    @DisplayName("장소 찜 생성 테스트")
    @Nested
    class Create {

        @DisplayName("장소 찜을 생성할 수 있다")
        @Test
        void create1() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);
            FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(false);
            given(favoritePlaceRepository.save(favoritePlace))
                    .willReturn(new FavoritePlace(1L, favoriteFolder, place));

            // when
            FavoritePlaceResponse response = favoritePlaceService.create(member, favoriteFolderId, placeId);

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(favoriteFolderId),
                    () -> assertThat(response.favoriteFolderId()).isEqualTo(favoriteFolderId),
                    () -> assertThat(response.placeId()).isEqualTo(placeId)
            );
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 ForbiddenException을 발생시킨다")
        @Test
        void create2() {
            // given
            String requestDeviceFid = "requestDeviceFid";
            String ownerDeviceFid = "ownerDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member owner = new Member(1L, ownerDeviceFid);
            Member requestMember = new Member(2L, requestDeviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, owner, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(requestMember, favoriteFolderId, placeId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void create3() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(member, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("해당 id에 대한 폴더가 존재하지 않습니다.");
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void create4() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(member, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("해당 id에 대한 장소가 존재하지 않습니다.");
        }

        @DisplayName("해당 폴더에 장소 찜이 되어있는 경우 ConflictException을 발생시킨다")
        @Test
        void create5() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(member, favoriteFolderId, placeId))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @DisplayName("장소 찜 삭제 테스트")
    @Nested
    class Remove {

        @DisplayName("장소 찜을 삭제할 수 있다")
        @Test
        void remove1() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);
            FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(Optional.of(favoritePlace));

            // when & then
            assertDoesNotThrow(() -> favoritePlaceService.remove(member, favoriteFolderId, placeId));
        }

        @DisplayName("폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우 ForbiddenException을 발생시킨다")
        @Test
        void remove2() {
            // given
            String requestDeviceFid = "requestDeviceFid";
            String ownerDeviceFid = "ownerDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member owner = new Member(1L, ownerDeviceFid);
            Member requestMember = new Member(2L, requestDeviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, owner, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(requestMember, favoriteFolderId, placeId))
                    .isInstanceOf(ForbiddenException.class);
        }

        @DisplayName("favoriteFolderId에 대한 폴더가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove3() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(member, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("해당 id에 대한 폴더가 존재하지 않습니다.");
        }

        @DisplayName("placeId에 대한 장소가 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove4() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(member, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("해당 id에 대한 장소가 존재하지 않습니다.");
        }

        @DisplayName("삭제하려는 장소 찜이 존재하지 않는 경우 NotFoundException을 발생시킨다")
        @Test
        void remove5() {
            // given
            String deviceFid = "testDeviceFid";
            Long favoriteFolderId = 1L;
            Long placeId = 1L;

            Member member = new Member(1L, deviceFid);
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, "폴더 이름 1", true);
            Place place = new Place(placeId, "장소 이름", "장소 url", "주소", 1, 1);

            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.remove(member, favoriteFolderId, placeId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("삭제하려는 장소 찜이 존재하지 않습니다.");
        }
    }
}
