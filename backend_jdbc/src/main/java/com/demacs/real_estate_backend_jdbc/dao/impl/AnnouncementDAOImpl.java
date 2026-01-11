package com.demacs.real_estate_backend_jdbc.dao.impl;

import com.demacs.real_estate_backend_jdbc.dao.AnnouncementDAO;
import com.demacs.real_estate_backend_jdbc.model.*;
import com.demacs.real_estate_backend_jdbc.dao.PropertyDAO;
import com.demacs.real_estate_backend_jdbc.dao.UserDAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AnnouncementDAOImpl implements AnnouncementDAO {

    private final JdbcTemplate jdbc;
    private final PropertyDAO propertyDAO;
    private final UserDAO userDAO;

    public AnnouncementDAOImpl(
            JdbcTemplate jdbc,
            PropertyDAO propertyDAO,
            UserDAO userDAO
    ) {
        this.jdbc = jdbc;
        this.propertyDAO = propertyDAO;
        this.userDAO = userDAO;
    }

    private static final String BASE_SELECT = """
        SELECT
            a.id AS a_id,
            a.titolo,
            a.descrizione,
            a.prezzo,
            a.tipo,
            a.image_url,
            a.data_pubblicazione,

            p.id AS p_id,
            p.tipo AS p_tipo,
            p.superficie_mq,
            p.stanze,
            p.bagni,
            p.address,
            p.city,
            p.latitude,
            p.longitude,

            u.id AS u_id,
            u.nome AS u_nome,
            u.cognome AS u_cognome,
            u.email AS u_email,
            u.ruolo AS u_ruolo

        FROM announcements a
        JOIN properties p ON a.property_id = p.id
        JOIN users u ON a.vendor_id = u.id
    """;

    @Override
    public List<Announcement> findAll() {
        return jdbc.query(BASE_SELECT, this::mapRow);
    }

    @Override
    public Optional<Announcement> findById(Long id) {
        List<Announcement> list = jdbc.query(
            BASE_SELECT + " WHERE a.id = ?",
            this::mapRow,
            id
        );
        return list.stream().findFirst();
    }

    @Override
    public List<Announcement> findByVenditoreId(Long venditoreId) {
        return jdbc.query(
            BASE_SELECT + " WHERE u.id = ?",
            this::mapRow,
            venditoreId
        );
    }

    @Override
    public Announcement save(Announcement announcement) {
        Property p = propertyDAO.save(announcement.getImmobile());

        String sql = """
            INSERT INTO announcements (
                titolo,
                descrizione,
                prezzo,
                tipo,
                image_url,
                property_id,
                vendor_id,
                data_pubblicazione
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        Long id = jdbc.queryForObject(
            sql,
            Long.class,
            announcement.getTitolo(),
            announcement.getDescrizione(),
            announcement.getPrezzo(),
            announcement.getTipo(),
            announcement.getImageUrl(),
            p.getId(),
            announcement.getVenditore().getId(),
            announcement.getDataPubblicazione() != null
                ? announcement.getDataPubblicazione()
                : LocalDate.now()
        );

        announcement.setId(id);
        announcement.setImmobile(p);
        return announcement;
    }

    @Override
    public void update(Announcement a) {
        propertyDAO.update(a.getImmobile());

        String sql = """
            UPDATE announcements SET
                titolo = ?,
                descrizione = ?,
                prezzo = ?,
                tipo = ?,
                image_url = ?
            WHERE id = ?
        """;

        jdbc.update(
            sql,
            a.getTitolo(),
            a.getDescrizione(),
            a.getPrezzo(),
            a.getTipo(),
            a.getImageUrl(),
            a.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM announcements WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM announcements WHERE id = ?",
            Integer.class,
            id
        );
        return count != null && count > 0;
    }

    private Announcement mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getLong("a_id"));
        a.setTitolo(rs.getString("titolo"));
        a.setDescrizione(rs.getString("descrizione"));
        a.setPrezzo(rs.getDouble("prezzo"));
        a.setTipo(rs.getString("tipo"));
        a.setImageUrl(rs.getString("image_url"));
        a.setDataPubblicazione(rs.getObject("data_pubblicazione", LocalDate.class));

        Property p = new Property();
        p.setId(rs.getLong("p_id"));
        p.setTipo(rs.getString("p_tipo"));
        p.setSuperficieMq(rs.getInt("superficie_mq"));
        p.setStanze(rs.getInt("stanze"));
        p.setBagni(rs.getInt("bagni"));

        Location loc = new Location();
        loc.setAddress(rs.getString("address"));
        loc.setCity(rs.getString("city"));
        loc.setLatitude(rs.getDouble("latitude"));
        loc.setLongitude(rs.getDouble("longitude"));

        p.setLocation(loc);
        a.setImmobile(p);

        User u = new User();
        u.setId(rs.getLong("u_id"));
        u.setNome(rs.getString("u_nome"));
        u.setCognome(rs.getString("u_cognome"));
        u.setEmail(rs.getString("u_email"));
        u.setRuolo(rs.getString("u_ruolo"));

        a.setVenditore(u);

        return a;
    }
}
