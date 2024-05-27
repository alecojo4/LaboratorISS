package com.example.proiectiss.repository;

import com.example.proiectiss.domain.User;

import java.util.Optional;

public interface UserRepository extends IRepository<User>{
    Optional<User> findByUsername(String username);
}
