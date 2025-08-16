package turip.favoritefolder.controller.dto.response;

import java.util.List;

public record FavoriteFoldersWithFavoriteStatusResponse(
        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFolders) {

    public static FavoriteFoldersWithFavoriteStatusResponse from(
            List<FavoriteFolderWithFavoriteStatusResponse> favoriteFolders) {
        return new FavoriteFoldersWithFavoriteStatusResponse(favoriteFolders);
    }
}
