package com.ecomm.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class InventoryServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
