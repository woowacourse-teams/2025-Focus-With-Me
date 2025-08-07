package turip.regioncategory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DomesticRegionCategoryTest {

    @DisplayName("국내에 존재하는 도시인지 아닌지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "서울, true",
            "서울시, true",
            "국내 기타, true",
            "일본, false"
    })
    void containsName(String category, boolean expected) {
        // when & then
        Assertions.assertThat(DomesticRegionCategory.containsName(category))
                .isEqualTo(expected);
    }

    @DisplayName("카테고리가 빈 값인 경우 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void containsName_withBlank_throwsIllegalArgumentException(String category) {
        // when & then
        assertThatThrownBy(() -> DomesticRegionCategory.containsName(category))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("국내 기타를 제외한 카테고리를 확인할 수 있다.")
    @Test
    void getDisplayNamesExcludingEtc() {
        // when & then
        assertThat(DomesticRegionCategory.getDisplayNamesExcludingEtc())
                .doesNotContain(DomesticRegionCategory.OTHER_DOMESTIC.getDisplayName());
    }
}
