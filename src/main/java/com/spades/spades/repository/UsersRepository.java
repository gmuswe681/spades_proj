package com.spades.spades.repository;

import com.spades.spades.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByName(String username);

    Optional<Users> findById(int id);
}