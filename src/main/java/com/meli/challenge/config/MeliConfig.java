package com.meli.challenge.config;

/**
 * Centralized configuration for MercadoLibre API interaction.
 */
public class MeliConfig {
    public static final String BASE_URL = "https://api.mercadolibre.com";
    public static final String SITE_ID = "MCO";
    public static final String USER_AGENT = "MeLi-Playground-App/1.0";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Endpoints
    public static final String SEARCH_ENDPOINT = BASE_URL + "/sites/" + SITE_ID + "/search";
    public static final String USERS_ME_ENDPOINT = BASE_URL + "/users/me";
    public static final String USER_ITEMS_SEARCH_TEMPLATE = BASE_URL + "/users/%d/items/search";
    public static final String ITEMS_DETAILS_ENDPOINT = BASE_URL + "/items";

    private MeliConfig() {
        // Prevent instantiation
    }
}
