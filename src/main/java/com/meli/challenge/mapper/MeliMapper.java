package com.meli.challenge.mapper;

import com.meli.challenge.model.Item;
import com.meli.challenge.model.MeliSearchResponse;

/**
 * Mapper for converting MercadoLibre API DTOs to domain models.
 */
public class MeliMapper {

    /**
     * Converts a search result item DTO to a domain Item.
     *
     * @param dto the DTO from MeLi API
     * @return the domain Item
     */
    public Item toDomain(MeliSearchResponse.SearchResultItem dto) {
        if (dto == null) {
            return null;
        }
        return Item.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .category(dto.getCategoryId())
                .price(dto.getPrice())
                .availableQuantity(dto.getAvailableQuantity())
                .build();
    }

    private MeliMapper() {
        // Factory or singleton pattern could be used, keeping it simple for now
    }
    
    public static MeliMapper getInstance() {
        return new MeliMapper();
    }
}
