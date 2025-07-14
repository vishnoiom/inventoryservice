package com.ecomm.inventoryservice.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ecomm.inventoryservice.model.Inventory;
import com.ecomm.inventoryservice.service.InventoryService;
import com.ecomm.inventoryservice.service.TokenService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
	@Autowired
    private InventoryService inventoryService;
	
	@Autowired
    TokenService tokenService;
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
	
	final String userType="STAFF";

	@GetMapping("/{productId}")
    public Optional<Inventory> getInventory(@PathVariable String productId) {
        return inventoryService.getInventory(productId);
    }

    @PutMapping("/decrease/{productId}")
    public Inventory decrease(@PathVariable String productId, @RequestParam int quantity) {
        return inventoryService.decreaseStock(productId, quantity);
    }

    @PutMapping("/increase/{productId}")
    public ResponseEntity<?>  increase(@PathVariable String productId, @RequestParam int quantity, @RequestHeader("Authorization") String tokenValue) {
    	String phone = null;
    	try
        {
            phone =  tokenService.validateToken(tokenValue, userType);
        }
        catch (WebClientResponseException e)
        {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getResponseBodyAsString());
        }
        Inventory savedInventory= inventoryService.increaseStock(productId, quantity);
        return ResponseEntity.status(201).body(savedInventory);
    }



}
