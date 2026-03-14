package com.meli.challenge.service;

import com.meli.challenge.model.Item;
import com.meli.challenge.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link ItemService}.
 */
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findAll() {
        log.debug("Fetching all items");
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> findById(String id) {
        log.debug("Fetching item with id={}", id);
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id must not be null or blank");
        }
        return itemRepository.findById(id);
    }

    @Override
    public Item save(Item item) {
        log.debug("Saving item: {}", item);
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        return itemRepository.save(item);
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting item with id={}", id);
        itemRepository.deleteById(id);
    }

    @Override
    public double getAveragePriceByCategory(String category) {
        log.debug("Calculating average price for category: {}", category);
        List<Item> items = itemRepository.findByCategory(category);
        
        return items.stream()
                .mapToDouble(Item::getPrice)
                .average()
                .orElse(0.0);
    }
}
