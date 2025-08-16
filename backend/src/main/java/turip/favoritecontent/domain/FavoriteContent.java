package turip.favoritecontent.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import turip.common.domain.TimeStamp;
import turip.content.domain.Content;
import turip.member.domain.Member;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is Null")
@SQLDelete(sql = "UPDATE favorite_content SET deleted_at = NOW() WHERE id = ?")
public class FavoriteContent extends TimeStamp {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Content content;

    public FavoriteContent(Member member, Content content) {
        this.member = member;
        this.content = content;
    }
}
