package com.demacs.real_estate_backend_jdbc.dao;

import com.demacs.real_estate_backend_jdbc.model.Property;
import java.util.Optional;

public interface PropertyDAO {

    Property save(Property property);

    void update(Property property);

    Optional<Property> findById(Long id);

    void deleteById(Long id);
}
