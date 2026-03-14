package com.meli.challenge.repository;

import com.meli.challenge.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Data access contract for {@link Item}.
 * Replace with your own storage strategy (in-memory, DB, REST client, etc.).
 */
public interface ItemRepository {

    List<Item> findAll();

    Optional<Item> findById(String id);

    Item save(Item item);
    
    List<Item> findByCategory(String category);

    void deleteById(String id);
}
