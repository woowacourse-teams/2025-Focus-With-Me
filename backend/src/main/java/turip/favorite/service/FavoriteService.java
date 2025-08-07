package turip.favorite.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteRequest;
import turip.favorite.controller.dto.response.FavoriteResponse;
import turip.favorite.domain.Favorite;
import turip.favorite.repository.FavoriteRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public FavoriteResponse create(FavoriteRequest request, String deviceFid) {
        Long contentId = request.contentId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = findOrCreateMember(deviceFid);
        if (favoriteRepository.existsByMemberIdAndContentId(member.getId(), content.getId())) {
            throw new BadRequestException("이미 찜한 컨텐츠입니다.");
        }
        Favorite favorite = new Favorite(LocalDate.now(), member, content);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    @Transactional
    public void remove(String deviceFid, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Favorite favorite = favoriteRepository.findByMemberIdAndContentId(member.getId(), content.getId())
                .orElseThrow(() -> new NotFoundException("해당 컨텐츠는 찜한 상태가 아닙니다."));
        favoriteRepository.delete(favorite);
    }

    private Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> memberRepository.save(new Member(deviceFid)));
    }

}
