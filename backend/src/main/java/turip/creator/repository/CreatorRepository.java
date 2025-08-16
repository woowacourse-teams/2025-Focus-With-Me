package turip.creator.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.creator.domain.Creator;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    
    Optional<Creator> findByChannelName(String channelName);
}
