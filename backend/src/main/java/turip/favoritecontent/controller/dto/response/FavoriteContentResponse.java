package turip.favoritecontent.controller.dto.response;

import java.time.LocalDateTime;
import turip.content.controller.dto.response.ContentResponse;
import turip.favoritecontent.domain.FavoriteContent;

public record FavoriteContentResponse(
        Long id,
        LocalDateTime createdAt,
        Long memberId,
        ContentResponse content
) {

    public static FavoriteContentResponse from(FavoriteContent favoriteContent) {
        return new FavoriteContentResponse(
                favoriteContent.getId(),
                favoriteContent.getCreatedAt(),
                favoriteContent.getMember().getId(),
                ContentResponse.of(favoriteContent.getContent(), true)
        );
    }
}
