package com.ecomm.inventoryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecomm.inventoryservice.model.Inventory;

public interface InventoryRepository extends MongoRepository<Inventory, String> {

}
