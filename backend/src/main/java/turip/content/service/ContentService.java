package turip.content.service;

import static turip.regioncategory.domain.DomesticRegionCategory.OTHER_DOMESTIC;
import static turip.regioncategory.domain.OverseasRegionCategory.OTHER_OVERSEAS;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentByCityResponse;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentDetailsByRegionCategoryResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.controller.dto.response.ContentSearchResultResponse;
import turip.content.controller.dto.response.ContentWithCreatorAndCityResponse;
import turip.content.controller.dto.response.ContentsByRegionCategoryResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.controller.dto.response.WeeklyPopularFavoriteContentResponse;
import turip.content.controller.dto.response.WeeklyPopularFavoriteContentsResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.favorite.repository.FavoriteRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.NotFoundException;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int EXTRA_FETCH_COUNT = 1;
    private static final int DAYS_UNTIL_SUNDAY = 6;
    private static final int ONE_WEEK = 1;

    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

    public ContentResponse getContentWithFavoriteStatus(Long contentId, String deviceFid) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        if (deviceFid == null) {
            return ContentResponse.of(content, false);
        }
        boolean isFavorite = memberRepository.findByDeviceFid(deviceFid)
                .map(member -> favoriteRepository.existsByMemberIdAndContentId(member.getId(), content.getId()))
                .orElse(false);

        return ContentResponse.of(content, isFavorite);
    }

    public ContentCountResponse countByRegionCategory(String regionCategory) {
        int count = calculateCountByRegionCategory(regionCategory);
        return ContentCountResponse.from(count);
    }

    public ContentsByRegionCategoryResponse findContentsByRegionCategory(
            String regionCategory,
            int size,
            long lastId
    ) {
        Slice<Content> contentSlice = findContentSlicesByRegionCategory(regionCategory, lastId, size);

        List<Content> contents = contentSlice.getContent();
        List<ContentDetailsByRegionCategoryResponse> contentDetails
                = convertContentsToContentDetailsByRegionResponses(contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsByRegionCategoryResponse.of(contentDetails, loadable, regionCategory);
    }

    public WeeklyPopularFavoriteContentsResponse findWeeklyPopularFavoriteContents(String deviceFid,
                                                                                   int topContentSize) {
        List<LocalDate> lastWeekPeriod = getLastWeekPeriod();
        LocalDate startDate = lastWeekPeriod.getFirst();
        LocalDate endDate = lastWeekPeriod.getLast();

        List<Content> popularContents = favoriteRepository.findPopularContentsByFavoriteBetweenDatesWithLimit(
                startDate, endDate, topContentSize);
        if (deviceFid == null) {
            return convertContentsToPopularContentsResponse(popularContents, false);
        }
        Member member = memberRepository.findByDeviceFid(deviceFid)
                .orElse(null);
        if (member == null) {
            return convertContentsToPopularContentsResponse(popularContents, false);
        }
        Set<Long> favoritedContentIds = findFavoritedContentIds(member, popularContents);

        return WeeklyPopularFavoriteContentsResponse.from(
                popularContents.stream()
                        .map(content -> WeeklyPopularFavoriteContentResponse.of(
                                content,
                                favoritedContentIds.contains(content.getId()),
                                calculateTripDuration(content)
                        ))
                        .toList()
        );
    }

    public ContentCountResponse countByKeyword(String keyword) {
        int count = contentRepository.countByKeywordContaining(keyword);
        return ContentCountResponse.from(count);
    }

    public ContentSearchResponse searchContentsByKeyword(
            String keyword,
            int pageSize,
            long lastContentId
    ) {
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contents = contentRepository.findByKeywordContaining(keyword, lastContentId,
                PageRequest.of(0, pageSize));
        boolean loadable = contents.hasNext();

        List<ContentSearchResultResponse> contentSearchResultResponses = convertContentsToContentSearchResultResponse(
                contents);

        return ContentSearchResponse.of(contentSearchResultResponses, loadable);
    }

    private int calculateCountByRegionCategory(String regionCategory) {
        if (OTHER_DOMESTIC.matchesDisplayName(regionCategory)) {
            return calculateDomesticEtcCount();
        }

        if (OTHER_OVERSEAS.matchesDisplayName(regionCategory)) {
            return calculateOverseasEtcCount();
        }

        if (DomesticRegionCategory.containsName(regionCategory)) {
            return contentRepository.countByCityName(regionCategory);
        }

        if (OverseasRegionCategory.containsName(regionCategory)) {
            return contentRepository.countByCityCountryName(regionCategory);
        }

        throw new BadRequestException("지역 카테고리가 올바르지 않습니다.");
    }

    private int calculateDomesticEtcCount() {
        List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countDomesticEtcContents(domesticCategoryNames);
    }

    private int calculateOverseasEtcCount() {
        List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countOverseasEtcContents(overseasCategoryNames);
    }

    private Slice<Content> findContentSlicesByRegionCategory(
            String regionCategory,
            long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size);
        boolean isFirstPage = lastId == 0;

        if (OTHER_DOMESTIC.matchesDisplayName(regionCategory)) {
            return findDomesticEtcContents(lastId, pageable, isFirstPage);
        }

        if (OTHER_OVERSEAS.matchesDisplayName(regionCategory)) {
            return findOverseasEtcContents(lastId, pageable, isFirstPage);
        }

        if (DomesticRegionCategory.containsName(regionCategory)) {
            return findContentsByCityName(regionCategory, lastId, pageable, isFirstPage);
        }

        return findContentsByCountryName(regionCategory, lastId, pageable, isFirstPage);
    }

    private Slice<Content> findDomesticEtcContents(long lastId, Pageable pageable, boolean isFirstPage) {
        List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();

        if (isFirstPage) {
            return contentRepository.findDomesticEtcContents(domesticCategoryNames, pageable);
        }
        return contentRepository.findDomesticEtcContentsWithLastId(domesticCategoryNames, lastId, pageable);
    }

    private Slice<Content> findOverseasEtcContents(long lastId, Pageable pageable, boolean isFirstPage) {
        List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();

        if (isFirstPage) {
            return contentRepository.findOverseasEtcContents(overseasCategoryNames, pageable);
        }
        return contentRepository.findOverseasEtcContentsWithLastId(overseasCategoryNames, lastId, pageable);
    }

    private Slice<Content> findContentsByCityName(String cityName, long lastId, Pageable pageable,
                                                  boolean isFirstPage) {
        if (isFirstPage) {
            return contentRepository.findByCityNameOrderByIdDesc(cityName, pageable);
        }
        return contentRepository.findByCityNameAndIdLessThanOrderByIdDesc(cityName, lastId, pageable);
    }

    private Slice<Content> findContentsByCountryName(String countryName, long lastId, Pageable pageable,
                                                     boolean isFirstPage) {
        if (isFirstPage) {
            return contentRepository.findByCityCountryNameOrderByIdDesc(countryName, pageable);
        }
        return contentRepository.findByCityCountryNameAndIdLessThanOrderByIdDesc(countryName, lastId, pageable);
    }

    private List<ContentDetailsByRegionCategoryResponse> convertContentsToContentDetailsByRegionResponses(
            List<Content> contents) {
        return contents.stream()
                .map(this::toContentDetailsByRegionResponse)
                .toList();
    }

    private ContentDetailsByRegionCategoryResponse toContentDetailsByRegionResponse(Content content) {
        ContentByCityResponse contentWithCity = ContentByCityResponse.from(content);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = tripCourseService.countByContentId(content.getId());

        return ContentDetailsByRegionCategoryResponse.of(contentWithCity, tripDuration, tripPlaceCount);
    }

    private List<ContentSearchResultResponse> convertContentsToContentSearchResultResponse(Slice<Content> contents) {
        return contents.stream()
                .map(this::toContentSearchResultResponse)
                .toList();
    }

    private ContentSearchResultResponse toContentSearchResultResponse(Content content) {
        int placeCount = tripCourseService.countByContentId(content.getId());

        return ContentSearchResultResponse.of(
                ContentWithCreatorAndCityResponse.from(content),
                calculateTripDuration(content),
                placeCount
        );
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<LocalDate> getLastWeekPeriod() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(ONE_WEEK);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(DAYS_UNTIL_SUNDAY);
        return new ArrayList<>(List.of(lastWeekMonday, lastWeekSunday));
    }

    private Set<Long> findFavoritedContentIds(Member member, List<Content> contents) {
        List<Long> contentIds = contents.stream()
                .map(Content::getId)
                .toList();
        return favoriteRepository.findByMemberIdAndContentIdIn(member.getId(), contentIds).stream()
                .map(favorite -> favorite.getContent().getId())
                .collect(Collectors.toSet());
    }

    private WeeklyPopularFavoriteContentsResponse convertContentsToPopularContentsResponse(
            List<Content> popularContents, boolean isFavorite) {
        return WeeklyPopularFavoriteContentsResponse.from(
                popularContents.stream()
                        .map(content -> WeeklyPopularFavoriteContentResponse.of(content, isFavorite,
                                calculateTripDuration(content)))
                        .toList()
        );
    }
}
