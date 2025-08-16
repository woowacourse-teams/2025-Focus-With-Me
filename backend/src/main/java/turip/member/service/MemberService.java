package turip.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Transactional
    public Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> {
                    Member savedMember = memberRepository.save(new Member(deviceFid));
                    FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedMember);
                    favoriteFolderRepository.save(defaultFolder);
                    return savedMember;
                });
    }

    public Member getMemberByDeviceId(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException("해당 id에 대한 회원이 존재하지 않습니다."));
    }
}
