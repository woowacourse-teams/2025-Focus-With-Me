package turip.favoritefolder.controller.dto.response;

import turip.favoritefolder.domain.FavoriteFolder;

public record FavoriteFolderWithFavoriteStatusResponse(
        Long id,
        Long memberId,
        String name,
        boolean isDefault,
        boolean isFavoritePlace
) {

    public static FavoriteFolderWithFavoriteStatusResponse of(FavoriteFolder favoriteFolder, boolean isFavoritePlace) {
        return new FavoriteFolderWithFavoriteStatusResponse(favoriteFolder.getId(), favoriteFolder.getMember().getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), isFavoritePlace);
    }
}
