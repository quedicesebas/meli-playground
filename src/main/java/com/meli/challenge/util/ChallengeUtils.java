package com.meli.challenge.util;

/**
 * General-purpose utility helpers.
 * Add static methods here as needed during the challenge.
 */
public final class ChallengeUtils {

    private ChallengeUtils() {
        // utility class — no instances
    }

    /**
     * Returns {@code true} if the given string is null or blank.
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Validates that a value is not null, throwing {@link IllegalArgumentException} with the given message.
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
