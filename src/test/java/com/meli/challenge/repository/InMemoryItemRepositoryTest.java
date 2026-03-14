package com.meli.challenge.repository;

import com.meli.challenge.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryItemRepository")
class InMemoryItemRepositoryTest {

    private InMemoryItemRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemRepository();
    }

    @Test
    @DisplayName("saves and retrieves an item by id")
    void saveAndFindById() {
        Item item = Item.builder().id("MLA1").title("Test Item").price(100.0).build();

        repository.save(item);
        Optional<Item> found = repository.findById("MLA1");

        assertThat(found).isPresent().contains(item);
    }

    @Test
    @DisplayName("findAll returns all saved items")
    void findAllReturnsSavedItems() {
        repository.save(Item.builder().id("MLA1").title("Item A").price(10.0).build());
        repository.save(Item.builder().id("MLA2").title("Item B").price(20.0).build());

        List<Item> all = repository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("deleteById removes the item")
    void deleteByIdRemovesItem() {
        Item item = Item.builder().id("MLA1").title("To Delete").price(50.0).build();
        repository.save(item);

        repository.deleteById("MLA1");

        assertThat(repository.findById("MLA1")).isEmpty();
    }

    @Test
    @DisplayName("findById returns empty Optional when item does not exist")
    void findByIdReturnsEmptyWhenMissing() {
        assertThat(repository.findById("NONEXISTENT")).isEmpty();
    }

    @Test
    @DisplayName("save overwrites an existing item with the same id")
    void saveOverwritesExistingItem() {
        Item original = Item.builder().id("MLA1").title("Original").price(100.0).build();
        Item updated  = Item.builder().id("MLA1").title("Updated").price(200.0).build();

        repository.save(original);
        repository.save(updated);

        assertThat(repository.findById("MLA1"))
                .isPresent()
                .hasValueSatisfying(i -> assertThat(i.getTitle()).isEqualTo("Updated"));
    }
}
