package turip.favoriteplace.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.exception.custom.ConflictException;
import turip.exception.custom.ForbiddenException;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.controller.dto.response.FavoritePlaceResponse;
import turip.favoriteplace.controller.dto.response.FavoritePlaceWithDetailPlaceInformationResponse;
import turip.favoriteplace.controller.dto.response.FavoritePlacesWithDetailPlaceInformationResponse;
import turip.favoriteplace.domain.FavoritePlace;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.member.service.MemberService;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoritePlaceRepository favoritePlaceRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final PlaceRepository placeRepository;
    private final MemberService memberService;

    @Transactional
    public FavoritePlaceResponse create(String deviceFid, Long favoriteFolderId, Long placeId) {
        Member member = memberService.findOrCreateMember(deviceFid);
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        validateOwnership(member, favoriteFolder);
        validateDuplicated(favoriteFolder, place);

        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place);
        FavoritePlace savedFavoritePlace = favoritePlaceRepository.save(favoritePlace);

        return FavoritePlaceResponse.from(savedFavoritePlace);
    }

    public FavoritePlacesWithDetailPlaceInformationResponse findAllByFolder(Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);

        List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces = favoritePlaceRepository.findAllByFavoriteFolder(
                        favoriteFolder).stream()
                .map(FavoritePlaceWithDetailPlaceInformationResponse::from)
                .toList();

        return FavoritePlacesWithDetailPlaceInformationResponse.from(favoritePlaces);
    }

    @Transactional
    public void remove(String deviceFid, Long favoriteFolderId, Long placeId) {
        Member member = memberService.getMemberByDeviceId(deviceFid);
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        validateOwnership(member, favoriteFolder);
        FavoritePlace favoritePlace = getByFavoriteFolderAndPlace(favoriteFolder, place);

        favoritePlaceRepository.delete(favoritePlace);
    }

    private FavoriteFolder getFavoriteFolderById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 폴더가 존재하지 않습니다."));
    }

    private Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 장소가 존재하지 않습니다."));
    }

    private void validateOwnership(Member requestMember, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestMember)) {
            throw new ForbiddenException("폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다.");
        }
    }

    private void validateDuplicated(FavoriteFolder favoriteFolder, Place place) {
        boolean isAlreadyFavorite = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place);
        if (isAlreadyFavorite) {
            throw new ConflictException("이미 해당 폴더에 찜한 장소입니다.");
        }
    }

    private FavoritePlace getByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place) {
        return favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place)
                .orElseThrow(() -> new NotFoundException("삭제하려는 장소 찜이 존재하지 않습니다."));
    }
}
