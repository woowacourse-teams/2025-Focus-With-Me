package turip.category.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
}
