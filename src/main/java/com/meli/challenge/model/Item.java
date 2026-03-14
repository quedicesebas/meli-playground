package com.meli.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Example domain model — replace/expand this according to the challenge requirements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private String id;
    private String title;
    private String category;
    private double price;
    private int availableQuantity;

}
