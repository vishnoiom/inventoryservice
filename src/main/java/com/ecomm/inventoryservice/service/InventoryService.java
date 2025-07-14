package com.ecomm.inventoryservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecomm.inventoryservice.model.Inventory;
import com.ecomm.inventoryservice.model.Product;
import com.ecomm.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InventoryService {
	@Autowired
    private InventoryRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    

    public Optional<Inventory> getInventory(String productId) {
        return repository.findById(productId);
    }

    public Inventory decreaseStock(String productId, int quantity) {
        Inventory inv = repository.findById(productId).orElseThrow();
        inv.setQuantity(inv.getQuantity() - quantity);
        return repository.save(inv);
    }

    public Inventory increaseStock(String productId, int quantity) {
        Inventory inv = repository.findById(productId).orElseThrow();
        inv.setQuantity(inv.getQuantity() + quantity);
        return repository.save(inv);
    }

    @KafkaListener(topics = "product-events")
    public void consumeProductEvent(String message) throws JsonMappingException, JsonProcessingException {
    	Product product = objectMapper.readValue(message, Product.class);
        if (!repository.existsById(product.getId())) {
            repository.save(new Inventory(product.getId(), 100)); // default initial stock
        }
    }


}
