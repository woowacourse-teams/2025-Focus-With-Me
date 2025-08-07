package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.city.domain.City;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.creator.domain.Creator;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteRequest;
import turip.favorite.controller.dto.response.FavoriteResponse;
import turip.favorite.domain.Favorite;
import turip.favorite.repository.FavoriteRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ContentRepository contentRepository;

    @DisplayName("찜을 생성할 수 있다")
    @Test
    void createFavorite() {
        // given
        Long contentId = 1L;
        String deviceFid = "testDeviceFid";

        FavoriteRequest request = new FavoriteRequest(contentId);
        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content = new Content(contentId, creator, city, null, null, null);
        Member member = new Member(deviceFid);
        Favorite favorite = new Favorite(LocalDate.now(), member, content);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(memberRepository.findByDeviceFid(deviceFid))
                .willReturn(Optional.of(member));
        given(favoriteRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(false);
        given(favoriteRepository.save(any(Favorite.class)))
                .willReturn(favorite);

        // when
        FavoriteResponse response = favoriteService.create(request, deviceFid);

        // then
        assertThat(response.content().id()).isEqualTo(contentId);
    }

    @DisplayName("컨텐츠가 존재하지 않는 경우 예외가 발생한다")
    @Test
    void createFavoriteException() {
        // given
        Long contentId = 10L;
        String deviceFid = "testDeviceFid";
        FavoriteRequest request = new FavoriteRequest(contentId);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteService.create(request, deviceFid))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("이미 찜한 컨텐츠인 경우 예외가 발생한다")
    @Test
    void createFavoriteException2() {
        // given
        Long contentId = 5L;
        String deviceFid = "testDeviceFid";
        FavoriteRequest request = new FavoriteRequest(contentId);

        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content = new Content(contentId, creator, city, null, null, null);
        Member member = new Member(deviceFid);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(memberRepository.findByDeviceFid(deviceFid))
                .willReturn(Optional.of(member));
        given(favoriteRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> favoriteService.create(request, deviceFid))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("찜 삭제 테스트")
    @Nested
    class Remove {

        @DisplayName("찜을 삭제할 수 있다")
        @Test
        void deleteFavorite1() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);
            Member member = new Member(deviceFid);
            Favorite favorite = new Favorite(LocalDate.now(), member, content);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.of(favorite));

            // when
            favoriteService.remove(deviceFid, contentId);

            // then
            assertThat(favoriteRepository.findById(contentId)).isEmpty();
        }

        @DisplayName("삭제하려는 찜의 contentId에 대한 컨텐츠가 존재하지 않으면 에러가 발생한다.")
        @Test
        void deleteFavorite2() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("삭제하려는 찜의 사용자가 존재하지 않으면 에러가 발생한다.")
        @Test
        void deleteFavorite3() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("삭제하려는 찜이 찜 상태가 아니라면 에러를 발생시킨다.")
        @Test
        void deleteFavorite4() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);
            Member member = new Member(deviceFid);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.of(member));
            given(favoriteRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
