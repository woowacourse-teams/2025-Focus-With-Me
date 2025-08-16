package turip.favoritefolder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import turip.common.domain.TimeStamp;
import turip.member.domain.Member;

@Getter
@Entity
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is Null")
@SQLDelete(sql = "UPDATE favorite_folder SET deleted_at = NOW() WHERE id = ?")
public class FavoriteFolder extends TimeStamp {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private String name;

    private boolean isDefault;

    public static FavoriteFolder defaultFolderOf(Member member) {
        return new FavoriteFolder(member, "기본 폴더", true);
    }

    public static FavoriteFolder customFolderOf(Member member, String name) {
        String formattedName = formatName(name);
        validateName(formattedName);
        return new FavoriteFolder(member, name, false);
    }

    public boolean isOwner(Member member) {
        return this.member.isSameDeviceId(member);
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public static String formatName(String unformattedName) {
        return unformattedName.trim();
    }

    private FavoriteFolder(Member member, String name, boolean isDefault) {
        this.member = member;
        this.name = name;
        this.isDefault = isDefault;
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("장소 찜 폴더 이름은 빈 칸이 될 수 없습니다.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("장소 찜 폴더 이름은 최대 20글자 입니다.");
        }
    }
}
