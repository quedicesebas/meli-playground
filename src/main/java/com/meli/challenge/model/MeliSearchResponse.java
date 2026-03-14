package com.meli.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeliSearchResponse {
    private List<SearchResultItem> results;
    @JsonProperty("results_ids")
    private List<String> resultsIds;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResultItem {
        private String id;
        private String title;
        private double price;
        @JsonProperty("category_id")
        private String categoryId;
        @JsonProperty("available_quantity")
        private int availableQuantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemDetailResponse {
        private int code;
        private SearchResultItem body;
    }
}
