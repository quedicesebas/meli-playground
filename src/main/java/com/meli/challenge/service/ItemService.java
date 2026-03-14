package com.meli.challenge.service;

import com.meli.challenge.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Contract for item-related business logic.
 * Define your own methods here as needed by the challenge.
 */
public interface ItemService {

    /**
     * Returns all available items.
     */
    List<Item> findAll();

    /**
     * Finds an item by its ID.
     */
    Optional<Item> findById(String id);

    /**
     * Saves or updates an item.
     */
    Item save(Item item);

    /**
     * Deletes an item by ID.
     */
    void deleteById(String id);

    /**
     * Calculates the average price for all items in a category.
     */
    double getAveragePriceByCategory(String category);
}
