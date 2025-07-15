package com.ecomm.inventoryservice.controller;

import com.ecomm.inventoryservice.model.Inventory;
import com.ecomm.inventoryservice.service.InventoryService;
import com.ecomm.inventoryservice.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@ActiveProfiles("test")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private TokenService tokenService;

    @Test
    void testGetInventory() throws Exception {
        Inventory inventory = new Inventory("prod123", 10);

        when(inventoryService.getInventory("prod123")).thenReturn(Optional.of(inventory));

        mockMvc.perform(get("/inventory/prod123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("prod123"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void testDecreaseStock() throws Exception {
        Inventory inventory = new Inventory("prod123", 8);

        when(inventoryService.decreaseStock("prod123", 2)).thenReturn(inventory);

        mockMvc.perform(put("/inventory/decrease/prod123")
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(8));
    }

    @Test
    void testIncreaseStock_ValidToken() throws Exception {
        Inventory inventory = new Inventory("prod123", 15);

        when(tokenService.validateToken("valid-token", "STAFF")).thenReturn("staff123");
        when(inventoryService.increaseStock("prod123", 5)).thenReturn(inventory);

        mockMvc.perform(put("/inventory/increase/prod123")
                .header("Authorization", "valid-token")
                .param("quantity", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    void testIncreaseStock_InvalidToken() throws Exception {
        when(tokenService.validateToken("invalid-token", "STAFF"))
                .thenThrow(new org.springframework.web.reactive.function.client.WebClientResponseException(
                        401, "Unauthorized", null, null, null));

        mockMvc.perform(put("/inventory/increase/prod123")
                .header("Authorization", "invalid-token")
                .param("quantity", "5"))
                .andExpect(status().isUnauthorized());
    }
}