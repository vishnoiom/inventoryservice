package com.ecomm.inventoryservice.model;

import lombok.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;


}
