package turip.content.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;

@DataJpaTest
@ActiveProfiles("test")
class ContentRepositoryTest {

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @DisplayName("키워드로 콘텐츠 및 크리에이터 이름을 검색해 개수를 반환하는 메서드 테스트")
    @Nested
    class CountByKeyword {

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들의 수를 확인할 수 있다.")
        @ParameterizedTest
        @CsvSource({
                "메이,3",
                "뭉치,2",
                "대구,2"
        })
        void countByKeyword1(String keyword, int expected) {
            // given
            // 기존 데이터가 있으면 사용하고, 없으면 새로 생성
            Country country = countryRepository.findByName("대한민국")
                    .orElseGet(() -> countryRepository.save(new Country("대한민국", "https://example.com/korea.jpg")));
            
            City city = cityRepository.findByName("서울")
                    .orElseGet(() -> cityRepository.save(new City(country, null, "서울", "https://example.com/seoul.jpg")));
            
            Creator may = new Creator("여행하는 메이", "https://example.com/may.jpg");
            Creator moong = new Creator("여행하는 뭉치", "https://example.com/moong.jpg");
            Content mayContent1 = new Content(may, city, "메이의 대구 여행", "https://example.com/may1", LocalDate.now());
            Content mayContent2 = new Content(may, city, "메이의 부산 여행", "https://example.com/may2", LocalDate.now());
            Content moongAndMayContent = new Content(moong, city, "뭉치와 메이의 서울 여행", "https://example.com/moong1", LocalDate.now());
            Content moongContent = new Content(moong, city, "뭉치의 대구 여행", "https://example.com/moong2", LocalDate.now());

            creatorRepository.saveAll(List.of(may, moong));
            contentRepository.saveAll(List.of(mayContent1, mayContent2, moongAndMayContent, moongContent));

            // when
            int count = contentRepository.countByKeywordContaining(keyword);

            // then
            assertThat(count)
                    .isEqualTo(expected);
        }
    }

    @DisplayName("페이지네이션 기반 키워드 검색 메서드 테스트")
    @Nested
    class FindByKeyword {

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들을 확인할 수 있다.")
        @ParameterizedTest
        @CsvSource({
                "메이,3",
                "뭉치,2",
                "대구,2"
        })
        void findByKeyword1(String keyword, int expected) {
            // given
            // 기존 데이터가 있으면 사용하고, 없으면 새로 생성
            Country country = countryRepository.findByName("대한민국")
                    .orElseGet(() -> countryRepository.save(new Country("대한민국", "https://example.com/korea.jpg")));
            
            City city = cityRepository.findByName("서울")
                    .orElseGet(() -> cityRepository.save(new City(country, null, "서울", "https://example.com/seoul.jpg")));
            
            Creator may = new Creator("여행하는 메이", "https://example.com/may.jpg");
            Creator moong = new Creator("여행하는 뭉치", "https://example.com/moong.jpg");
            Content mayContent1 = new Content(may, city, "메이의 대구 여행", "https://example.com/may1", LocalDate.now());
            Content mayContent2 = new Content(may, city, "메이의 부산 여행", "https://example.com/may2", LocalDate.now());
            Content moongAndMayContent = new Content(moong, city, "뭉치와 메이의 서울 여행", "https://example.com/moong1", LocalDate.now());
            Content moongContent = new Content(moong, city, "뭉치의 대구 여행", "https://example.com/moong2", LocalDate.now());

            creatorRepository.saveAll(List.of(may, moong));
            contentRepository.saveAll(List.of(mayContent1, mayContent2, moongAndMayContent, moongContent));

            // when
            Slice<Content> contents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(0, 5));

            // then
            assertThat(contents.getContent().size())
                    .isEqualTo(expected);
        }

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들을 원하는 수만큼만 가져올 수 있다")
        @Test
        void findByKeyword2() {
            // given
            String keyword = "메이";
            // 기존 데이터가 있으면 사용하고, 없으면 새로 생성
            Country country = countryRepository.findByName("대한민국")
                    .orElseGet(() -> countryRepository.save(new Country("대한민국", "https://example.com/korea.jpg")));
            
            City city = cityRepository.findByName("서울")
                    .orElseGet(() -> cityRepository.save(new City(country, null, "서울", "https://example.com/seoul.jpg")));
            
            Creator creator = new Creator("여행하는 " + keyword, "https://example.com/creator.jpg");
            Content content1 = new Content(creator, city, keyword + "의 대구 여행", "https://example.com/content1", LocalDate.now());
            Content content2 = new Content(creator, city, keyword + "의 부산 여행", "https://example.com/content2", LocalDate.now());
            Content content3 = new Content(creator, city, keyword + "의 서울 여행", "https://example.com/content3", LocalDate.now());
            Content content4 = new Content(creator, city, keyword + "의 춘천 여행", "https://example.com/content4", LocalDate.now());
            Content content5 = new Content(creator, city, keyword + "의 강릉 여행", "https://example.com/content5", LocalDate.now());
            Content content6 = new Content(creator, city, keyword + "의 후쿠오카 여행", "https://example.com/content6", LocalDate.now());
            Content content7 = new Content(creator, city, keyword + "의 다낭 여행", "https://example.com/content7", LocalDate.now());

            creatorRepository.save(creator);
            List<Content> savedContents = contentRepository.saveAll(
                    List.of(content1, content2, content3, content4, content5, content6, content7));

            // when
            int pageSize = 5;
            int totalSize = savedContents.size();
            Slice<Content> firstPageContents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(0, pageSize));
            Slice<Content> secondPageContents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(1, pageSize));

            // then
            Assertions.assertAll(
                    () -> assertThat(firstPageContents.getContent().size())
                            .isEqualTo(pageSize),
                    () -> assertThat(secondPageContents.getContent().size())
                            .isEqualTo(totalSize - pageSize)
            );
        }
    }
}
