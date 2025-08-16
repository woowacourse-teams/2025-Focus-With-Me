package turip.favoritefolder.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.ConflictException;
import turip.exception.custom.ForbiddenException;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.controller.dto.request.FavoriteFolderNameRequest;
import turip.favoritefolder.controller.dto.request.FavoriteFolderRequest;
import turip.favoritefolder.controller.dto.response.FavoriteFolderResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFolderWithPlaceCountResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favoritefolder.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.member.service.MemberService;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final MemberService memberService;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public FavoriteFolderResponse createCustomFavoriteFolder(FavoriteFolderRequest request, String deviceFid) {
        Member member = memberService.findOrCreateMember(deviceFid);
        FavoriteFolder favoriteFolder = FavoriteFolder.customFolderOf(member, request.name());

        validateDuplicatedName(favoriteFolder.getName(), member);
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(favoriteFolder);

        return FavoriteFolderResponse.from(savedFavoriteFolder);
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByDeviceFid(String deviceFid) {
        Member member = memberService.findOrCreateMember(deviceFid);
        List<FavoriteFolderWithPlaceCountResponse> favoriteFoldersWithPlaceCount = favoriteFolderRepository.findAllByMember(
                        member).stream()
                .map(favoriteFolder -> {
                    int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
                    return FavoriteFolderWithPlaceCountResponse.of(favoriteFolder, placeCount);
                })
                .toList();

        return FavoriteFoldersWithPlaceCountResponse.from(favoriteFoldersWithPlaceCount);
    }

    public FavoriteFoldersWithFavoriteStatusResponse findAllWithFavoriteStatusByDeviceId(String deviceFid,
                                                                                         Long placeId) {
        Member member = memberService.findOrCreateMember(deviceFid);
        Place place = getPlaceById(placeId);

        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFoldersWithFavoriteStatus = favoriteFolderRepository.findAllByMember(
                        member).stream()
                .map(favoriteFolder -> {
                    boolean isFavoritePlace = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder,
                            place);
                    return FavoriteFolderWithFavoriteStatusResponse.of(favoriteFolder, isFavoritePlace);
                })
                .toList();

        return FavoriteFoldersWithFavoriteStatusResponse.from(favoriteFoldersWithFavoriteStatus);
    }

    @Transactional
    public FavoriteFolderResponse updateName(String deviceFid, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        Member member = memberService.getMemberByDeviceId(deviceFid);
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException("기본 폴더는 수정할 수 없습니다.");
        }

        String newName = FavoriteFolder.formatName(request.name());
        validateOwnership(member, favoriteFolder);
        validateDuplicatedName(newName, member);
        favoriteFolder.rename(newName);

        return FavoriteFolderResponse.from(favoriteFolder);
    }

    @Transactional
    public void remove(String deviceFid, Long favoriteFolderId) {
        Member member = memberService.getMemberByDeviceId(deviceFid);
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        if (favoriteFolder.isDefault()) {
            throw new BadRequestException("기본 폴더는 삭제할 수 없습니다.");
        }
        validateOwnership(member, favoriteFolder);

        favoriteFolderRepository.deleteById(favoriteFolderId);
    }

    private void validateDuplicatedName(String folderName, Member member) {
        if (favoriteFolderRepository.existsByNameAndMember(folderName, member)) {
            throw new ConflictException("중복된 폴더 이름이 존재합니다.");
        }
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 장소가 존재하지 않습니다."));
    }

    private FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 폴더가 존재하지 않습니다."));
    }

    private void validateOwnership(Member requestMember, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestMember)) {
            throw new ForbiddenException("폴더 소유자의 기기id와 요청자의 기기id가 같지 않습니다.");
        }
    }
}
