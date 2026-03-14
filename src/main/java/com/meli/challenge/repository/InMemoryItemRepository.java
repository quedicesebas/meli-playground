package com.meli.challenge.repository;

import com.meli.challenge.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory repository backed by a {@link ConcurrentHashMap}.
 * Useful for challenges that do not require a real database.
 */
public class InMemoryItemRepository implements ItemRepository {

    private final Map<String, Item> store = new ConcurrentHashMap<>();

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Item> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Item save(Item item) {
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findByCategory(String category) {
        return store.values().stream()
                .filter(item -> item.getCategory() != null && item.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
