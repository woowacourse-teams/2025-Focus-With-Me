package turip.content.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findByTitleAndUrl(String title, String url);

    int countByCityName(String cityName);

    @Query("""
                SELECT COUNT(c) FROM Content c
                JOIN c.city ct
                JOIN ct.country co
                WHERE ct.name NOT IN :cityNames AND co.name = '대한민국'
            """)
    int countDomesticEtcContents(List<String> cityNames);

    int countByCityCountryName(@Param("countryName") String countryName);

    @Query("""
                SELECT COUNT(c) FROM Content c
                JOIN c.city ct
                JOIN ct.country co
                WHERE co.name NOT IN :countryNames AND co.name != '대한민국'
            """)
    int countOverseasEtcContents(@Param("countryNames") List<String> countryNames);

    Slice<Content> findByCityNameOrderByIdDesc(String cityName, Pageable pageable);

    Slice<Content> findByCityNameAndIdLessThanOrderByIdDesc(String cityName, Long lastId, Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE ct.name NOT IN :domesticCategoryNames AND co.name = '대한민국'
            ORDER BY c.id DESC
            """)
    Slice<Content> findDomesticEtcContents(@Param("domesticCategoryNames") List<String> domesticCategoryNames,
                                           Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE ct.name NOT IN :domesticCategoryNames AND co.name = '대한민국' AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findDomesticEtcContentsWithLastId(@Param("domesticCategoryNames") List<String> domesticCategoryNames,
                                                     @Param("lastId") Long lastId, Pageable pageable);

    Slice<Content> findByCityCountryNameOrderByIdDesc(String countryName, Pageable pageable);

    Slice<Content> findByCityCountryNameAndIdLessThanOrderByIdDesc(String countryName, Long lastId, Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE co.name NOT IN :overseasCategoryNames AND co.name != '대한민국'
            ORDER BY c.id DESC
            """)
    Slice<Content> findOverseasEtcContents(@Param("overseasCategoryNames") List<String> overseasCategoryNames,
                                           Pageable pageable);

    @Query("""
            SELECT c FROM Content c JOIN c.city ct JOIN ct.country co
            WHERE co.name NOT IN :overseasCategoryNames AND co.name != '대한민국' AND c.id < :lastId
            ORDER BY c.id DESC
            """)
    Slice<Content> findOverseasEtcContentsWithLastId(@Param("overseasCategoryNames") List<String> overseasCategoryNames,
                                                     @Param("lastId") Long lastId, Pageable pageable);

    @Query("""
                SELECT count(c) FROM Content c
                JOIN c.creator cr
                WHERE c.title LIKE %:keyword% OR cr.channelName LIKE %:keyword%
            """)
    int countByKeywordContaining(String keyword);

    @Query("""
                SELECT c FROM Content c
                JOIN c.creator cr
                WHERE c.id < :lastId
                AND (c.title LIKE %:keyword% OR cr.channelName LIKE %:keyword%)
                ORDER BY c.id DESC
            """)
    Slice<Content> findByKeywordContaining(String keyword, Long lastId, Pageable pageable);
}
