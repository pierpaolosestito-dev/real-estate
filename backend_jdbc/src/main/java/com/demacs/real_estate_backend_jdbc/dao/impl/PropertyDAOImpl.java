package com.demacs.real_estate_backend_jdbc.dao.impl;

import com.demacs.real_estate_backend_jdbc.dao.PropertyDAO;
import com.demacs.real_estate_backend_jdbc.model.Property;
import com.demacs.real_estate_backend_jdbc.model.Location;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PropertyDAOImpl implements PropertyDAO {

    private final JdbcTemplate jdbc;

    public PropertyDAOImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE_SELECT = """
        SELECT
            id,
            tipo,
            superficie_mq,
            stanze,
            bagni,
            address,
            city,
            latitude,
            longitude
        FROM properties
    """;

    @Override
    public Optional<Property> findById(Long id) {
        List<Property> list = jdbc.query(
            BASE_SELECT + " WHERE id = ?",
            this::mapRow,
            id
        );
        return list.stream().findFirst();
    }

    @Override
    public Property save(Property p) {
        String sql = """
            INSERT INTO properties (
                tipo,
                superficie_mq,
                stanze,
                bagni,
                address,
                city,
                latitude,
                longitude
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        Long id = jdbc.queryForObject(
            sql,
            Long.class,
            p.getTipo(),
            p.getSuperficieMq(),
            p.getStanze(),
            p.getBagni(),
            p.getLocation() != null ? p.getLocation().getAddress() : null,
            p.getLocation() != null ? p.getLocation().getCity() : null,
            p.getLocation() != null ? p.getLocation().getLatitude() : null,
            p.getLocation() != null ? p.getLocation().getLongitude() : null
        );

        p.setId(id);
        return p;
    }

    @Override
    public void update(Property p) {
        String sql = """
            UPDATE properties SET
                tipo = ?,
                superficie_mq = ?,
                stanze = ?,
                bagni = ?,
                address = ?,
                city = ?,
                latitude = ?,
                longitude = ?
            WHERE id = ?
        """;

        jdbc.update(
            sql,
            p.getTipo(),
            p.getSuperficieMq(),
            p.getStanze(),
            p.getBagni(),
            p.getLocation() != null ? p.getLocation().getAddress() : null,
            p.getLocation() != null ? p.getLocation().getCity() : null,
            p.getLocation() != null ? p.getLocation().getLatitude() : null,
            p.getLocation() != null ? p.getLocation().getLongitude() : null,
            p.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM properties WHERE id = ?", id);
    }

    private Property mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Property p = new Property();
        p.setId(rs.getLong("id"));
        p.setTipo(rs.getString("tipo"));
        p.setSuperficieMq(rs.getInt("superficie_mq"));
        p.setStanze(rs.getInt("stanze"));
        p.setBagni(rs.getInt("bagni"));

        Location loc = new Location();
        loc.setAddress(rs.getString("address"));
        loc.setCity(rs.getString("city"));
        loc.setLatitude(rs.getDouble("latitude"));
        loc.setLongitude(rs.getDouble("longitude"));

        p.setLocation(loc);

        return p;
    }
}
