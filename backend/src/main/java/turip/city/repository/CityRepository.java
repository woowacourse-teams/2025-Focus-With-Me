package turip.city.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.city.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByCountryName(String name);
    
    Optional<City> findByName(String name);
}
