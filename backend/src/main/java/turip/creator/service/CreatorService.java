package turip.creator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.exception.custom.NotFoundException;

@Service
@RequiredArgsConstructor
public class CreatorService {

    private final CreatorRepository creatorRepository;

    public CreatorResponse getById(Long id) {
        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("크리에이터를 찾을 수 없습니다."));
        return CreatorResponse.from(creator);
    }
}
