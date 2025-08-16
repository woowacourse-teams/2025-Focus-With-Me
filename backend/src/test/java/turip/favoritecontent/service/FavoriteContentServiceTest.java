package turip.favoritecontent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import turip.city.domain.City;
import turip.content.controller.dto.response.MyFavoriteContentsResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.contentplace.service.ContentPlaceService;
import turip.creator.domain.Creator;
import turip.exception.custom.ConflictException;
import turip.exception.custom.NotFoundException;
import turip.favoritecontent.controller.dto.request.FavoriteContentRequest;
import turip.favoritecontent.controller.dto.response.FavoriteContentResponse;
import turip.favoritecontent.domain.FavoriteContent;
import turip.favoritecontent.repository.FavoriteContentRepository;
import turip.member.domain.Member;
import turip.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class FavoriteContentServiceTest {

    @InjectMocks
    private FavoriteContentService favoriteContentService;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentPlaceService contentPlaceService;

    @Mock
    private MemberService memberService;

    @DisplayName("찜을 생성할 수 있다")
    @Test
    void createFavoriteContent() {
        // given
        Long contentId = 1L;
        String deviceFid = "testDeviceFid";

        FavoriteContentRequest request = new FavoriteContentRequest(contentId);
        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content = new Content(contentId, creator, city, null, null, null);
        Member member = new Member(deviceFid);
        FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(memberService.findOrCreateMember(deviceFid))
                .willReturn(new Member(1L, deviceFid));
        given(favoriteContentRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(false);
        given(favoriteContentRepository.save(any(FavoriteContent.class)))
                .willReturn(favoriteContent);

        // when
        FavoriteContentResponse response = favoriteContentService.create(request, deviceFid);

        // then
        assertThat(response.content().id()).isEqualTo(contentId);
    }

    @DisplayName("컨텐츠가 존재하지 않는 경우 예외가 발생한다")
    @Test
    void createFavoriteException() {
        // given
        Long contentId = 10L;
        String deviceFid = "testDeviceFid";
        FavoriteContentRequest request = new FavoriteContentRequest(contentId);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteContentService.create(request, deviceFid))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("이미 찜한 컨텐츠인 경우 예외가 발생한다")
    @Test
    void createFavoriteException2() {
        // given
        Long contentId = 5L;
        String deviceFid = "testDeviceFid";
        FavoriteContentRequest request = new FavoriteContentRequest(contentId);

        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content = new Content(contentId, creator, city, null, null, null);
        Member member = new Member(deviceFid);

        given(contentRepository.findById(contentId))
                .willReturn(Optional.of(content));
        given(memberService.findOrCreateMember(deviceFid))
                .willReturn(new Member(1L, deviceFid));
        given(favoriteContentRepository.existsByMemberIdAndContentId(any(), eq(contentId)))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> favoriteContentService.create(request, deviceFid))
                .isInstanceOf(ConflictException.class);
    }

    @DisplayName("내 찜 목록을 페이징 조회할 수 있다")
    @Test
    void findMyFavoriteContents1() {
        // given
        String deviceFid = "testDeviceFid";
        int pageSize = 2;
        long lastContentId = 0L;

        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content1 = new Content(1L, creator, city, null, null, null);
        Content content2 = new Content(2L, creator, city, null, null, null);
        List<Content> contents = List.of(content1, content2);

        given(favoriteContentRepository.findMyFavoriteContentsByDeviceFid(eq(deviceFid), eq(Long.MAX_VALUE), any()))
                .willReturn(new SliceImpl<>(contents));
        given(contentPlaceService.calculateDurationDays(1L))
                .willReturn(2); // content1 1박 2일
        given(contentPlaceService.calculateDurationDays(2L))
                .willReturn(3); // content2 2박 3일
        given(contentRepository.existsById(1L))
                .willReturn(true);
        given(contentRepository.existsById(2L))
                .willReturn(true);

        MyFavoriteContentsResponse response = favoriteContentService.findMyFavoriteContents(deviceFid, pageSize,
                lastContentId);

        // then
        assertAll(
                () -> assertThat(response.contents()).hasSize(2),
                () -> assertThat(response.loadable()).isFalse(),
                () -> assertThat(response.contents().getFirst().tripDuration().days()).isEqualTo(2),
                () -> assertThat(response.contents().get(1).tripDuration().days()).isEqualTo(3)
        );
    }

    @DisplayName("내 찜 목록을 페이징 조회할 때, 컨텐츠 id에 대한 컨텐츠가 존재하지 않는 경우 NotFoundException을 발생시킨다.")
    @Test
    void findMyFavoriteContents2() {
        // given
        String deviceFid = "testDeviceFid";
        int pageSize = 2;
        long lastContentId = 0L;

        Creator creator = new Creator(null, null);
        City city = new City(null, null, null, null);
        Content content1 = new Content(1L, creator, city, null, null, null);
        Content content2 = new Content(2L, creator, city, null, null, null);
        List<Content> contents = List.of(content1, content2);

        given(favoriteContentRepository.findMyFavoriteContentsByDeviceFid(eq(deviceFid), eq(Long.MAX_VALUE), any()))
                .willReturn(new SliceImpl<>(contents));
        given(contentPlaceService.calculateDurationDays(1L))
                .willReturn(2); // content1 1박 2일
        given(contentPlaceService.calculateDurationDays(2L))
                .willReturn(3); // content2 2박 3일
        given(contentRepository.existsById(1L))
                .willReturn(true);
        given(contentRepository.existsById(2L))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> favoriteContentService.findMyFavoriteContents(deviceFid, pageSize, lastContentId))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("찜 삭제 테스트")
    @Nested
    class RemoveFavoriteContent {

        @DisplayName("찜을 삭제할 수 있다")
        @Test
        void deleteFavoriteContent1() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);
            Member member = new Member(deviceFid);
            FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberService.getMemberByDeviceId(deviceFid))
                    .willReturn(member);
            given(favoriteContentRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.of(favoriteContent));

            // when
            favoriteContentService.remove(deviceFid, contentId);

            // then
            assertThat(favoriteContentRepository.findById(contentId)).isEmpty();
        }

        @DisplayName("삭제하려는 찜의 contentId에 대한 컨텐츠가 존재하지 않으면 에러가 발생한다.")
        @Test
        void deleteFavoriteContent2() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteContentService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("삭제하려는 찜의 사용자가 존재하지 않으면 에러가 발생한다.")
        @Test
        void deleteFavoriteContent3() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberService.getMemberByDeviceId(deviceFid))
                    .willThrow(NotFoundException.class);

            // when & then
            assertThatThrownBy(() -> favoriteContentService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("삭제하려는 찜이 찜 상태가 아니라면 에러를 발생시킨다.")
        @Test
        void deleteFavoriteContent4() {
            // given
            Long contentId = 1L;
            String deviceFid = "testDeviceFid";
            Creator creator = new Creator(null, null);
            City city = new City(null, null, null, null);
            Content content = new Content(contentId, creator, city, null, null, null);
            Member member = new Member(deviceFid);

            given(contentRepository.findById(contentId))
                    .willReturn(Optional.of(content));
            given(memberService.getMemberByDeviceId(deviceFid))
                    .willReturn(member);
            given(favoriteContentRepository.findByMemberIdAndContentId(any(), eq(contentId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> favoriteContentService.remove(deviceFid, contentId))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
