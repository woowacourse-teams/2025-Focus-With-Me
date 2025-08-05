package turip.content.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByCityName(String provinceName);

    List<Content> findByCityNameAndIdLessThanOrderByIdDesc(String provinceName, Long lastId, Pageable pageable);

    List<Content> findByCityNameOrderByIdDesc(String provinceName, Pageable pageable);

    int countByCityCountryName(@Param("countryName") String countryName);

    int countByCityNameNotIn(List<String> cityNames);

    @Query("SELECT COUNT(c) FROM Content c JOIN c.city.country co WHERE co.name NOT IN :countryNames")
    int countByCountryNameNotIn(@Param("countryNames") List<String> countryNames);

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
