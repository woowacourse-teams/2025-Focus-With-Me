package turip.favoriteplace.controller.dto.response;

import java.util.List;

public record FavoritePlacesWithDetailPlaceInformationResponse(
        List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces, Integer favoritePlaceCount) {

    public static FavoritePlacesWithDetailPlaceInformationResponse from(
            List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces) {
        return new FavoritePlacesWithDetailPlaceInformationResponse(favoritePlaces, favoritePlaces.size());
    }
}
