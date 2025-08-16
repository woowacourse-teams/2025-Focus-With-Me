package turip.favoriteplace.controller.dto.response;

import turip.favoriteplace.domain.FavoritePlace;
import turip.place.controller.dto.response.PlaceResponse;

public record FavoritePlaceWithDetailPlaceInformationResponse(Long id, PlaceResponse place) {

    public static FavoritePlaceWithDetailPlaceInformationResponse from(FavoritePlace favoritePlace) {
        return new FavoritePlaceWithDetailPlaceInformationResponse(favoritePlace.getId(),
                PlaceResponse.from(favoritePlace.getPlace()));
    }
}
