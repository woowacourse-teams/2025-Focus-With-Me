package turip.favoriteplace.domain;

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
import turip.favoritefolder.domain.FavoriteFolder;
import turip.place.domain.Place;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is Null")
@SQLDelete(sql = "UPDATE favorite_place SET deleted_at = NOW() WHERE id = ?")
public class FavoritePlace extends TimeStamp {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FavoriteFolder favoriteFolder;

    @ManyToOne
    private Place place;
}
