package com.meli.challenge.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ChallengeUtils")
class ChallengeUtilsTest {

    @Test
    @DisplayName("isBlank returns true for null")
    void isBlankReturnsTrueForNull() {
        assertThat(ChallengeUtils.isBlank(null)).isTrue();
    }

    @Test
    @DisplayName("isBlank returns true for empty string")
    void isBlankReturnsTrueForEmpty() {
        assertThat(ChallengeUtils.isBlank("")).isTrue();
    }

    @Test
    @DisplayName("isBlank returns true for blank string")
    void isBlankReturnsTrueForBlank() {
        assertThat(ChallengeUtils.isBlank("   ")).isTrue();
    }

    @Test
    @DisplayName("isBlank returns false for non-blank string")
    void isBlankReturnsFalseForNonBlank() {
        assertThat(ChallengeUtils.isBlank("hello")).isFalse();
    }

    @Test
    @DisplayName("requireNonNull returns value when not null")
    void requireNonNullReturnsValue() {
        String result = ChallengeUtils.requireNonNull("value", "must not be null");
        assertThat(result).isEqualTo("value");
    }

    @Test
    @DisplayName("requireNonNull throws when value is null")
    void requireNonNullThrowsWhenNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ChallengeUtils.requireNonNull(null, "must not be null"))
                .withMessage("must not be null");
    }
}
