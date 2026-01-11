package com.demacs.real_estate_backend_jdbc.dao;

import com.demacs.real_estate_backend_jdbc.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmailIgnoreCase(String email);

    User save(User user);

    void update(User user);

    void deleteById(Long id);

    boolean existsById(Long id);
}
