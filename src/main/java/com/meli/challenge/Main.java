package com.meli.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meli.challenge.client.MeliClient;
import com.meli.challenge.mapper.MeliMapper;
import com.meli.challenge.model.Item;
import com.meli.challenge.repository.InMemoryItemRepository;
import com.meli.challenge.repository.ItemRepository;
import com.meli.challenge.service.ItemService;
import com.meli.challenge.service.ItemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Application entry point.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("MercadoLibre Playground - Starting application");

        // 0. Load environment variables from .env.local if it exists
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
                .filename(".env.local")
                .ignoreIfMissing()
                .load();

        // 1. Setup dependencies
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        
        // Try environment variable first, then .env.local
        String accessToken = System.getenv("MELI_ACCESS_TOKEN");
        if (accessToken == null || accessToken.isBlank()) {
            accessToken = dotenv.get("MELI_ACCESS_TOKEN");
        }
        
        MeliMapper meliMapper = new MeliMapper();
        MeliClient client = new MeliClient(mapper, meliMapper, accessToken);
        ItemRepository repository = new InMemoryItemRepository();
        ItemService service = new ItemServiceImpl(repository);

        // 2. Fetch data from MeLi API
        String query = "Lente";
        List<Item> items;
        try {
            items = client.searchItems(query);
        } catch (com.meli.challenge.exception.MeliApiException e) {
            log.error("API Error: {}. Using fallback mock data.", e.getMessage());
            items = ((ItemServiceImpl) service).getFallbackItems(query);
        }

        // 3. Store and process data
        items.forEach(service::save);

        // 4. Generate Report
        printReport(query, items, service);

        log.info("Done.");
    }

    private static void printReport(String query, List<Item> items, ItemService service) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" MERCADOLIBRE PLAYGROUND - REPORT ");
        System.out.println("=".repeat(60));
        System.out.printf("Search Query:     %s\n", query);
        System.out.printf("Items Found:      %d\n", items.size());
        
        if (!items.isEmpty()) {
            // Calculate some stats using the service
            String firstCategory = items.get(0).getCategory();
            double avgPrice = service.getAveragePriceByCategory(firstCategory);
            
            System.out.printf("Average Price (Cat: %s): $%.2f\n", firstCategory, avgPrice);
            
            System.out.println("-".repeat(60));
            System.out.printf("%-15s | %-30s | %-10s\n", "ID", "Title", "Price");
            System.out.println("-".repeat(60));
            
            items.stream().limit(5).forEach(i -> {
                String title = i.getTitle().length() > 30 ? i.getTitle().substring(0, 27) + "..." : i.getTitle();
                System.out.printf("%-15s | %-30s | $%.2f\n", i.getId(), title, i.getPrice());
            });
        }
        System.out.println("=".repeat(60) + "\n");
    }
}
