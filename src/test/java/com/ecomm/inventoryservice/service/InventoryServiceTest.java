package com.ecomm.inventoryservice.service;

import com.ecomm.inventoryservice.model.Inventory;
import com.ecomm.inventoryservice.model.Product;
import com.ecomm.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository repository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventory = new Inventory("prod123", 20);
    }

    @Test
    void testGetInventory() {
        when(repository.findById("prod123")).thenReturn(Optional.of(inventory));

        Optional<Inventory> result = inventoryService.getInventory("prod123");

        assertTrue(result.isPresent());
        assertEquals("prod123", result.get().getProductId());
    }

    @Test
    void testDecreaseStock() {
        when(repository.findById("prod123")).thenReturn(Optional.of(inventory));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Inventory result = inventoryService.decreaseStock("prod123", 5);

        assertEquals(15, result.getQuantity());
        verify(repository).save(any());
    }

    @Test
    void testIncreaseStock() {
        when(repository.findById("prod123")).thenReturn(Optional.of(inventory));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Inventory result = inventoryService.increaseStock("prod123", 10);

        assertEquals(30, result.getQuantity());
        verify(repository).save(any());
    }

    @Test
    void testConsumeProductEvent_NewProduct() throws JsonProcessingException {
        Product product = new Product("prod456", "Phone", "Smartphone", null);
        String jsonMessage = "{\"id\":\"prod456\",\"name\":\"Phone\",\"description\":\"Smartphone\"}";

        when(objectMapper.readValue(jsonMessage, Product.class)).thenReturn(product);
        when(repository.existsById("prod456")).thenReturn(false);

        inventoryService.consumeProductEvent(jsonMessage);

        verify(repository).save(new Inventory("prod456", 100));
    }

    @Test
    void testConsumeProductEvent_ExistingProduct() throws JsonProcessingException {
        Product product = new Product("prod456", "Phone", "Smartphone", null);
        String jsonMessage = "{\"id\":\"prod456\",\"name\":\"Phone\",\"description\":\"Smartphone\"}";

        when(objectMapper.readValue(jsonMessage, Product.class)).thenReturn(product);
        when(repository.existsById("prod456")).thenReturn(true);

        inventoryService.consumeProductEvent(jsonMessage);

        verify(repository, never()).save(any());
    }
}
