package com.couture.couturebackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;        // ex: Jaquette chirurgicale L2
    private String description; // ex: Tissu haute résistance, lavable
    private String category;    // ex: Hôpital, Cuisine, Literie
    private String material;    // ex: Coton, Polyester
    private String imageUrl;    // Le lien vers la photo du vêtement
}