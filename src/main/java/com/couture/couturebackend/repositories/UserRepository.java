package com.couture.couturebackend.repositories;

import com.couture.couturebackend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // pour aller chercher un utilisateur par son email
    User findByEmail(String email);
}