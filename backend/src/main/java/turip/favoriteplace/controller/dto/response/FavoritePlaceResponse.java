package turip.favoriteplace.controller.dto.response;

import turip.favoriteplace.domain.FavoritePlace;

public record FavoritePlaceResponse(
        Long id,
        Long favoriteFolderId,
        Long placeId
) {

    public static FavoritePlaceResponse from(FavoritePlace savedFavoritePlace) {
        return new FavoritePlaceResponse(savedFavoritePlace.getId(), savedFavoritePlace.getFavoriteFolder().getId(),
                savedFavoritePlace.getPlace().getId());
    }
}
