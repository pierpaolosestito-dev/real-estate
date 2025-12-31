package com.example.real_estate_backend.repository;

import com.example.real_estate_backend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
