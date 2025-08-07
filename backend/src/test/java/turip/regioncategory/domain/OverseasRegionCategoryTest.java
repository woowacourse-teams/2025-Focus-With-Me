package turip.regioncategory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class OverseasRegionCategoryTest {

    @DisplayName("해외에 존재하는 나라인지 아닌지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "서울, false",
            "서울시, false",
            "일본, true",
            "해외 기타, true"
    })
    void containsName(String category, boolean expected) {
        // when & then
        Assertions.assertThat(OverseasRegionCategory.containsName(category))
                .isEqualTo(expected);
    }

    @DisplayName("카테고리가 빈 값인 경우 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void containsName_withBlank_throwsIllegalArgumentException(String category) {
        // when & then
        assertThatThrownBy(() -> OverseasRegionCategory.containsName(category))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("해외 기타를 제외한 카테고리를 확인할 수 있다.")
    @Test
    void getDisplayNamesExcludingEtc() {
        // when & then
        assertThat(OverseasRegionCategory.getDisplayNamesExcludingEtc())
                .doesNotContain(OverseasRegionCategory.OTHER_OVERSEAS.getDisplayName());
    }
}
