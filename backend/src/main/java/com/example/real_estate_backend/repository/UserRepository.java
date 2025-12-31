package com.example.real_estate_backend.repository;

import com.example.real_estate_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
