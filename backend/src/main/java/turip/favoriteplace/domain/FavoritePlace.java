package turip.favoriteplace.domain;

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
import turip.favoritefolder.domain.FavoriteFolder;
import turip.place.domain.Place;

@Getter
@Entity
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoritePlace {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FavoriteFolder favoriteFolder;

    @ManyToOne
    private Place place;

    public FavoritePlace(FavoriteFolder favoriteFolder, Place place) {
        this.favoriteFolder = favoriteFolder;
        this.place = place;
    }
}
