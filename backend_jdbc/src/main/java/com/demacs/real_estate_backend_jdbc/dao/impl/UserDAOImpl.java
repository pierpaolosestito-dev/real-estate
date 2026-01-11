package com.demacs.real_estate_backend_jdbc.dao.impl;

import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import com.demacs.real_estate_backend_jdbc.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbc;

    public UserDAOImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE_SELECT = """
        SELECT id, nome, cognome, email, ruolo, password
        FROM users
    """;

    @Override
    public List<User> findAll() {
        return jdbc.query(BASE_SELECT, this::mapRow);
    }

    @Override
    public Optional<User> findById(Long id) {
        List<User> list = jdbc.query(
            BASE_SELECT + " WHERE id = ?",
            this::mapRow,
            id
        );
        return list.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        List<User> list = jdbc.query(
            BASE_SELECT + " WHERE LOWER(email) = LOWER(?)",
            this::mapRow,
            email
        );
        return list.stream().findFirst();
    }

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (nome, cognome, email, ruolo, password)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """;

        Long id = jdbc.queryForObject(
            sql,
            Long.class,
            user.getNome(),
            user.getCognome(),
            user.getEmail(),
            user.getRuolo(),
            user.getPassword()
        );

        user.setId(id);
        return user;
    }

    @Override
    public void update(User user) {
        String sql = """
            UPDATE users SET
                nome = ?,
                cognome = ?,
                email = ?,
                ruolo = ?
            WHERE id = ?
        """;

        jdbc.update(
            sql,
            user.getNome(),
            user.getCognome(),
            user.getEmail(),
            user.getRuolo(),
            user.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM users WHERE id = ?",
            Integer.class,
            id
        );
        return count != null && count > 0;
    }

    private User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setRuolo(rs.getString("ruolo"));
        u.setPassword(rs.getString("password")); // serve per login
        return u;
    }
}
