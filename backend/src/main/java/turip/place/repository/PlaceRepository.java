package turip.place.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByName(String name);
}
