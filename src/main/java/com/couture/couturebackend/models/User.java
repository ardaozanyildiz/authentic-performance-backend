package com.couture.couturebackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String companyName; // Très important pour les clients de l'entreprise
    private String email;
    private String password;    // Ce mot de passe sera crypté plus tard
}