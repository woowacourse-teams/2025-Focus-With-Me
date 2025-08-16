package turip.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.exception.custom.NotFoundException;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @DisplayName("deviceFid를 기반으로 회원을 찾거나 생성하는 기능 테스트")
    @Nested
    class FindOrCreateMember {

        @DisplayName("기기 id에 대한 회원이 존재하지 않는 경우, 회원을 생성하고 기본 폴더를 추가한다")
        @Test
        void findOrCreateMember2() {
            // given
            String deviceFid = "testDeviceFid";
            Member member = new Member(deviceFid);
            Member savedMember = new Member(1L, member.getDeviceFid());
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());
            given(memberRepository.save(member))
                    .willReturn(savedMember);

            // when
            memberService.findOrCreateMember(deviceFid);

            // then
            FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedMember);
            verify(favoriteFolderRepository).save(defaultFolder);
        }
    }

    @DisplayName("deviceFid를 기반으로 회원을 찾는 테스트")
    @Nested
    class GetMemberByDeviceId {

        @DisplayName("기기 id에 대한 회원이 존재하지 않는 경우 NotFoundException을 발생시킨다.")
        @Test
        void getMemberByDeviceId1() {
            // given
            String deviceFid = "testDeviceFid";
            given(memberRepository.findByDeviceFid(deviceFid))
                    .willReturn(Optional.empty());

            // when
            memberService.findOrCreateMember(deviceFid);

            // then
            assertThatThrownBy(() -> memberService.getMemberByDeviceId(deviceFid))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
