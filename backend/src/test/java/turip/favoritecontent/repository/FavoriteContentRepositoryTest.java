package turip.favoritecontent.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.favoritecontent.domain.FavoriteContent;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@DataJpaTest
@ActiveProfiles("test")
class FavoriteContentRepositoryTest {

    @Autowired
    private FavoriteContentRepository favoriteContentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("컨텐츠를 최신 찜 기준으로 정렬하여 반환한다")
    void findMyFavoriteContentsByDeviceFid() {
        // given
        Country country = countryRepository.findByName("대한민국")
                .orElseGet(() -> countryRepository.save(new Country("대한민국", "https://example.com/korea.jpg")));

        City city = cityRepository.findByName("서울")
                .orElseGet(() -> cityRepository.save(new City(country, null, "서울", "https://example.com/seoul.jpg")));

        Creator creator = creatorRepository.save(new Creator("테스트 크리에이터", "https://example.com/profile.jpg"));

        Content content1 = contentRepository.save(
                new Content(creator, city, "테스트 컨텐츠 1", "https://example.com/content1", LocalDate.now()));
        Content content2 = contentRepository.save(
                new Content(creator, city, "테스트 컨텐츠 2", "https://example.com/content2", LocalDate.now()));

        Member member = memberRepository.save(new Member("testDeviceFid"));

        FavoriteContent favoriteContent1 = new FavoriteContent(member, content1);
        FavoriteContent favoriteContent2 = new FavoriteContent(member, content2);

        favoriteContentRepository.save(favoriteContent1);
        favoriteContentRepository.save(favoriteContent2);

        // when
        Slice<Content> result = favoriteContentRepository.findMyFavoriteContentsByDeviceFid(
                member.getDeviceFid(), Long.MAX_VALUE, PageRequest.of(0, 10));
        List<Content> contents = result.getContent();

        // then
        assertThat(contents.get(0).getId()).isEqualTo(content2.getId());
        assertThat(contents.get(1).getId()).isEqualTo(content1.getId());
    }
}
