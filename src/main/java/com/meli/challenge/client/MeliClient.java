package com.meli.challenge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.challenge.config.MeliConfig;
import com.meli.challenge.exception.MeliApiException;
import com.meli.challenge.mapper.MeliMapper;
import com.meli.challenge.model.Item;
import com.meli.challenge.model.MeliSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client for interacting with MercadoLibre API.
 */
public class MeliClient {
    private static final Logger log = LoggerFactory.getLogger(MeliClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MeliMapper meliMapper;
    private final String accessToken;
    private Long userId;

    public MeliClient(ObjectMapper objectMapper, String accessToken) {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = objectMapper;
        this.meliMapper = MeliMapper.getInstance();
        this.accessToken = accessToken;
    }

    /**
     * Verifies the access token and retrieves the user ID.
     * 
     * @throws MeliApiException if the token check fails
     */
    private void checkToken() {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MeliConfig.USERS_ME_ENDPOINT))
                    .header(MeliConfig.AUTH_HEADER, MeliConfig.BEARER_PREFIX + accessToken)
                    .header("User-Agent", MeliConfig.USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                var node = objectMapper.readTree(response.body());
                if (node.has("id")) {
                    this.userId = node.get("id").asLong();
                    log.info("Authenticated in MeLi as User ID: {}", userId);
                }
            } else {
                log.error("MeLi Token Check Failed - Status: {} Body: {}", response.statusCode(), response.body());
                throw new MeliApiException("Invalid or expired MeLi access token", response.statusCode());
            }
        } catch (MeliApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeliApiException("Unexpected error during token check", 500, e);
        }
    }

    /**
     * Searches for items based on a query.
     * 
     * @param query the search term
     * @return list of items found
     * @throws MeliApiException if the search fails
     */
    public List<Item> searchItems(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }

        log.info("Searching items on MeLi for: {}", query);
        checkToken();

        // If authenticated, we prioritize searching own items to avoid 403 on global search
        if (accessToken != null && userId != null) {
            return searchOwnItems(query);
        }

        return performPublicSearch(query);
    }

    private List<Item> performPublicSearch(String query) {
        try {
            String url = MeliConfig.SEARCH_ENDPOINT + "?q=" + query.replace(" ", "%20");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", MeliConfig.USER_AGENT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new MeliApiException("MeLi Public Search returned non-200 status", response.statusCode());
            }

            MeliSearchResponse searchResponse = objectMapper.readValue(response.body(), MeliSearchResponse.class);
            return searchResponse.getResults().stream()
                    .map(meliMapper::toDomain)
                    .collect(Collectors.toList());

        } catch (MeliApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeliApiException("Error during MeLi public search: " + e.getMessage(), 500, e);
        }
    }

    private List<Item> searchOwnItems(String query) {
        log.info("Attempting private search for own items (User ID: {})", userId);
        try {
            // Step 1: List Own Item IDs
            String listUrl = String.format(MeliConfig.USER_ITEMS_SEARCH_TEMPLATE, userId) + "?q=" + query.replace(" ", "%20");
            HttpRequest listRequest = HttpRequest.newBuilder()
                    .uri(URI.create(listUrl))
                    .header(MeliConfig.AUTH_HEADER, MeliConfig.BEARER_PREFIX + accessToken)
                    .header("User-Agent", MeliConfig.USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> listResponse = httpClient.send(listRequest, HttpResponse.BodyHandlers.ofString());
            
            if (listResponse.statusCode() != 200) {
                throw new MeliApiException("Failed to list user items", listResponse.statusCode());
            }

            var node = objectMapper.readTree(listResponse.body());
            var resultsNode = node.get("results");
            
            if (resultsNode == null || resultsNode.isEmpty()) {
                log.info("No items found for current user matching query: {}", query);
                return List.of();
            }

            String ids = "";
            for (var idNode : resultsNode) {
                if (!ids.isEmpty()) ids += ",";
                ids += idNode.asText();
            }

            // Step 2: Fetch Details for those IDs
            log.info("Fetching details for own items: {}", ids);
            String detailUrl = MeliConfig.ITEMS_DETAILS_ENDPOINT + "?ids=" + ids;
            HttpRequest detailRequest = HttpRequest.newBuilder()
                    .uri(URI.create(detailUrl))
                    .header(MeliConfig.AUTH_HEADER, MeliConfig.BEARER_PREFIX + accessToken)
                    .header("User-Agent", MeliConfig.USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> detailResponse = httpClient.send(detailRequest, HttpResponse.BodyHandlers.ofString());
            
            if (detailResponse.statusCode() != 200) {
                throw new MeliApiException("Failed to fetch item details", detailResponse.statusCode());
            }

            List<MeliSearchResponse.ItemDetailResponse> details = objectMapper.readValue(
                detailResponse.body(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, MeliSearchResponse.ItemDetailResponse.class)
            );

            return details.stream()
                    .filter(d -> d.getCode() == 200 && d.getBody() != null)
                    .map(d -> meliMapper.toDomain(d.getBody()))
                    .collect(Collectors.toList());

        } catch (MeliApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeliApiException("Error during MeLi private search: " + e.getMessage(), 500, e);
        }
    }
}
