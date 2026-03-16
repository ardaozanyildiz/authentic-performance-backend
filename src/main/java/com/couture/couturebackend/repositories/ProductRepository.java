package com.couture.couturebackend.repositories;

import com.couture.couturebackend.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // pour ramene des outils que mongoRepository a (save, findAll, delete)
}