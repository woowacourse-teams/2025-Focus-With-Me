package turip.favoriteplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import turip.member.service.MemberService;
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

    @Mock
    private MemberService memberService;

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
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, null, true);
            Place place = new Place(placeId, null, null, null, 1, 1);
            FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place);

            given(memberService.findOrCreateMember(deviceFid))
                    .willReturn(member);
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(false);
            given(favoritePlaceRepository.save(favoritePlace))
                    .willReturn(new FavoritePlace(1L, favoriteFolder, place));

            // when
            FavoritePlaceResponse response = favoritePlaceService.create(deviceFid, favoriteFolderId, placeId);

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
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, owner, null, true);
            Place place = new Place(placeId, null, null, null, 1, 1);

            given(memberService.findOrCreateMember(requestDeviceFid))
                    .willReturn(requestMember);
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(requestDeviceFid, favoriteFolderId, placeId))
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

            given(memberService.findOrCreateMember(deviceFid))
                    .willReturn(member);
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(deviceFid, favoriteFolderId, placeId))
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
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, null, true);

            given(memberService.findOrCreateMember(deviceFid))
                    .willReturn(member);
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(deviceFid, favoriteFolderId, placeId))
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
            FavoriteFolder favoriteFolder = new FavoriteFolder(favoriteFolderId, member, null, true);
            Place place = new Place(placeId, null, null, null, 1, 1);

            given(memberService.findOrCreateMember(deviceFid))
                    .willReturn(member);
            given(favoriteFolderRepository.findById(favoriteFolderId))
                    .willReturn(Optional.of(favoriteFolder));
            given(placeRepository.findById(placeId))
                    .willReturn(Optional.of(place));
            given(favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> favoritePlaceService.create(deviceFid, favoriteFolderId, placeId))
                    .isInstanceOf(ConflictException.class);
        }
    }
}
