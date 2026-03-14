package com.meli.challenge.service;

import com.meli.challenge.model.Item;
import com.meli.challenge.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemServiceImpl")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = Item.builder()
                .id("MLA123456")
                .title("Notebook Lenovo")
                .category("Electronics")
                .price(999.99)
                .availableQuantity(10)
                .build();
    }

    // ------------------------------------------------------------------ //
    //  findAll
    // ------------------------------------------------------------------ //
    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("returns all items from the repository")
        void returnsAllItems() {
            when(itemRepository.findAll()).thenReturn(List.of(sampleItem));

            List<Item> result = itemService.findAll();

            assertThat(result).hasSize(1).containsExactly(sampleItem);
            verify(itemRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("returns empty list when there are no items")
        void returnsEmptyListWhenNoItems() {
            when(itemRepository.findAll()).thenReturn(List.of());

            assertThat(itemService.findAll()).isEmpty();
        }
    }

    // ------------------------------------------------------------------ //
    //  findById
    // ------------------------------------------------------------------ //
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns item when it exists")
        void returnsItemWhenExists() {
            when(itemRepository.findById("MLA123456")).thenReturn(Optional.of(sampleItem));

            Optional<Item> result = itemService.findById("MLA123456");

            assertThat(result).isPresent().contains(sampleItem);
        }

        @Test
        @DisplayName("returns empty Optional when item does not exist")
        void returnsEmptyWhenNotFound() {
            when(itemRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

            assertThat(itemService.findById("UNKNOWN")).isEmpty();
        }

        @Test
        @DisplayName("throws IllegalArgumentException for null id")
        void throwsExceptionForNullId() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> itemService.findById(null));

            verifyNoInteractions(itemRepository);
        }

        @Test
        @DisplayName("throws IllegalArgumentException for blank id")
        void throwsExceptionForBlankId() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> itemService.findById("   "));

            verifyNoInteractions(itemRepository);
        }
    }

    // ------------------------------------------------------------------ //
    //  save
    // ------------------------------------------------------------------ //
    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("saves and returns the item")
        void savesItem() {
            when(itemRepository.save(sampleItem)).thenReturn(sampleItem);

            Item saved = itemService.save(sampleItem);

            assertThat(saved).isEqualTo(sampleItem);
            verify(itemRepository).save(sampleItem);
        }

        @Test
        @DisplayName("throws IllegalArgumentException for null item")
        void throwsExceptionForNullItem() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> itemService.save(null));

            verifyNoInteractions(itemRepository);
        }
    }

    // ------------------------------------------------------------------ //
    //  deleteById
    // ------------------------------------------------------------------ //
    @Nested
    @DisplayName("deleteById()")
    class DeleteById {

        @Test
        @DisplayName("delegates deletion to the repository")
        void delegatesToRepository() {
            itemService.deleteById("MLA123456");

            verify(itemRepository).deleteById("MLA123456");
        }
    }

    // ------------------------------------------------------------------ //
    //  getAveragePriceByCategory
    // ------------------------------------------------------------------ //
    @Nested
    @DisplayName("getAveragePriceByCategory()")
    class GetAveragePriceByCategory {

        @Test
        @DisplayName("calculates the correct average for items in the category")
        void calculatesCorrectAverage() {
            Item item2 = Item.builder().id("2").price(500.0).category("Electronics").build();
            when(itemRepository.findByCategory("Electronics")).thenReturn(List.of(sampleItem, item2));

            double average = itemService.getAveragePriceByCategory("Electronics");

            // sampleItem.price is 999.99, item2.price is 500.0
            // (999.99 + 500.0) / 2 = 749.995
            assertThat(average).isEqualTo(749.995, offset(0.001));
        }

        @Test
        @DisplayName("returns 0.0 when no items match the category")
        void returnsZeroWhenNoItems() {
            when(itemRepository.findByCategory("Empty")).thenReturn(List.of());

            assertThat(itemService.getAveragePriceByCategory("Empty")).isZero();
        }
    }
}
