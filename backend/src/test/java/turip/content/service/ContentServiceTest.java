package turip.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import turip.city.domain.City;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.controller.dto.response.WeeklyPopularFavoriteContentsResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.contentplace.service.ContentPlaceService;
import turip.country.domain.Country;
import turip.creator.domain.Creator;
import turip.exception.custom.BadRequestException;
import turip.favoritecontent.domain.FavoriteContent;
import turip.favoritecontent.repository.FavoriteContentRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentPlaceService contentPlaceService;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("키워드 기반 검색 기능 테스트")
    @Nested
    class FindContentsByKeyword {

        @DisplayName("lastContentId가 0인 경우, lastContentId가 Long.MAX_VALUE로 치환되어 id가 가장 큰 컨텐츠부터 순서대로 pageSize만큼 읽어온다.")
        @Test
        void findContentsByKeyword1() {
            // given
            String keyword = "메이";
            long lastContentId = 0L;
            int pageSize = 2;
            Long maxId = Long.MAX_VALUE;

            Creator creator = new Creator("메이", null);
            Country korea = new Country("대한민국", null);
            City seoul = new City(korea, null, "서울", null);

            List<Content> contents = List.of(new Content(1L, creator, seoul, null, null, null),
                    new Content(2L, creator, seoul, null, null, null));
            given(contentRepository.findByKeywordContaining(keyword, maxId, PageRequest.of(0, pageSize)))
                    .willReturn(new SliceImpl<>(contents));
            given(contentRepository.existsById(1L))
                    .willReturn(true);
            given(contentRepository.existsById(2L))
                    .willReturn(true);

            // when
            ContentSearchResponse contentsByKeyword = contentService.searchContentsByKeyword(keyword, pageSize,
                    lastContentId);

            // then
            assertThat(contentsByKeyword.loadable())
                    .isFalse();
        }
    }

    @DisplayName("주간 인기 찜 컨텐츠 조회 테스트")
    @Nested
    class FindWeeklyPopularFavoriteContents {

        @Test
        void findWeeklyPopularFavoriteContents() {
            // given
            LocalDate startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate endDate = startDate.plusDays(6);
            int topContentSize = 2;

            Creator creator = new Creator("하루", null);
            Country korea = new Country("대한민국", null);
            City seoul = new City(korea, null, "서울", null);

            Content content1 = new Content(1L, creator, seoul, null, null, null);
            Content content2 = new Content(2L, creator, seoul, null, null, null);

            Member member = new Member(1L, "testDeviceFid");

            List<Content> popularContents = List.of(content1, content2);

            given(favoriteContentRepository.findPopularContentsByFavoriteBetweenDatesWithLimit(startDate, endDate,
                    topContentSize))
                    .willReturn(popularContents);
            given(memberRepository.findByDeviceFid("testDeviceFid"))
                    .willReturn(Optional.of(member));
            given(favoriteContentRepository.findByMemberIdAndContentIdIn(1L, List.of(1L, 2L)))
                    .willReturn(List.of(new FavoriteContent(member, content1),
                            new FavoriteContent(member, content2)));
            given(contentPlaceService.calculateDurationDays(content1.getId()))
                    .willReturn(3); // content1, 2박 3일
            given(contentPlaceService.calculateDurationDays(content2.getId()))
                    .willReturn(2); // content2, 1박 2일

            // when
            WeeklyPopularFavoriteContentsResponse response = contentService.findWeeklyPopularFavoriteContents(
                    "testDeviceFid", topContentSize);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.contents()).hasSize(2),
                    () -> assertThat(response.contents().getFirst().content().id()).isEqualTo(1L),
                    () -> assertThat(response.contents().getFirst().content().isFavorite()).isTrue(),
                    () -> assertThat(response.contents().getFirst().tripDuration().days()).isEqualTo(3),
                    () -> assertThat(response.contents().get(1).content().id()).isEqualTo(2L),
                    () -> assertThat(response.contents().get(1).content().isFavorite()).isTrue(),
                    () -> assertThat(response.contents().get(1).tripDuration().days()).isEqualTo(2)
            );
        }
    }

    @DisplayName("지역별 카테고리 기반 컨텐츠 수 조회 기능 테스트")
    @Nested
    class CountContentByRegionCategory {
        @DisplayName("국내 특정 도시(서울) 조회 시 해당 도시의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withSeoul_returnsSeoulContentCount() {
            // given
            String regionCategory = "서울";
            int expectedCount = 5;
            given(contentRepository.countByCityName(regionCategory))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("해외 특정 국가(일본) 조회 시 해당 국가의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withJapan_returnsJapanContentCount() {
            // given
            String regionCategory = "일본";
            int expectedCount = 3;
            given(contentRepository.countByCityCountryName(regionCategory))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("국내 기타 조회 시 DomesticRegionCategory에 없는 도시들의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withDomesticEtc_returnsOtherDomesticContentCount() {
            // given
            String regionCategory = "국내 기타";
            int expectedCount = 10;
            List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
            given(contentRepository.countDomesticEtcContents(domesticCategoryNames))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("해외 기타 조회 시 OverseasRegionCategory에 없는 국가들의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withOverseasEtc_returnsOtherOverseasContentCount() {
            // given
            String regionCategory = "해외 기타";
            int expectedCount = 7;
            List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
            given(contentRepository.countOverseasEtcContents(overseasCategoryNames))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("지역 카테고리가 올바르지 않은 경우 BadRequest를 발생시킨다.")
        @Test
        void countByRegionCategory_withWrongCategory_throwsNotFoundException() {
            // given
            String regionCategory = "아라키스";

            // when & then
            assertThatThrownBy(() -> contentService.countByRegionCategory(regionCategory))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}
