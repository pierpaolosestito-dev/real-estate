package com.demacs.real_estate_backend_jdbc.dao.impl;

import com.demacs.real_estate_backend_jdbc.dao.AnnouncementReviewDAO;
import com.demacs.real_estate_backend_jdbc.model.Review;
import com.demacs.real_estate_backend_jdbc.model.User;
import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AnnouncementReviewDAOImpl implements AnnouncementReviewDAO {

    private final JdbcTemplate jdbc;
    private final UserDAO userDAO;

    public AnnouncementReviewDAOImpl(JdbcTemplate jdbc, UserDAO userDAO) {
        this.jdbc = jdbc;
        this.userDAO = userDAO;
    }

    private static final String BASE_SELECT = """
        SELECT 
            r.id AS r_id,
            r.announcement_id AS r_announcement_id,
            r.user_id AS r_user_id,
            r.rating,
            r.commento,
            r.created_at,
            u.id AS u_id,
            u.nome AS u_nome,
            u.cognome AS u_cognome,
            u.email AS u_email,
            u.ruolo AS u_ruolo
        FROM announcement_reviews r
        JOIN users u ON r.user_id = u.id
    """;

    @Override
    public List<Review> findByAnnouncementId(Long announcementId) {
        return jdbc.query(
            BASE_SELECT + " WHERE r.announcement_id = ? ORDER BY r.created_at DESC",
            this::mapRow,
            announcementId
        );
    }

    @Override
    public Optional<Review> findByAnnouncementIdAndUserId(Long announcementId, Long userId) {
        List<Review> list = jdbc.query(
            BASE_SELECT + " WHERE r.announcement_id = ? AND r.user_id = ?",
            this::mapRow,
            announcementId, userId
        );
        return list.stream().findFirst();
    }

    @Override
    public Review saveOrUpdate(Review review) {
        if (review.getId() != null) {
            String sql = """
                UPDATE announcement_reviews SET
                    rating = ?,
                    commento = ?,
                    created_at = ?
                WHERE id = ?
            """;

            jdbc.update(
                sql,
                review.getRating(),
                review.getCommento(),
                Timestamp.valueOf(review.getCreatedAt()),
                review.getId()
            );

            return review;
        }

        String sql = """
            INSERT INTO announcement_reviews (
                announcement_id,
                user_id,
                rating,
                commento,
                created_at
            ) VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """;

        Long id = jdbc.queryForObject(
            sql,
            Long.class,
            review.getAnnouncementId(),
            review.getUser().getId(),
            review.getRating(),
            review.getCommento(),
            review.getCreatedAt() != null
                ? Timestamp.valueOf(review.getCreatedAt())
                : Timestamp.valueOf(LocalDateTime.now())
        );

        review.setId(id);
        return review;
    }

    @Override
    public void deleteByAnnouncementIdAndUserId(Long announcementId, Long userId) {
        jdbc.update(
            "DELETE FROM announcement_reviews WHERE announcement_id = ? AND user_id = ?",
            announcementId, userId
        );
    }

    @Override
    public void deleteByAnnouncementId(Long announcementId) {
        jdbc.update(
            "DELETE FROM announcement_reviews WHERE announcement_id = ?",
            announcementId
        );
    }

    @Override
    public long countByAnnouncementId(Long announcementId) {
        Long count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM announcement_reviews WHERE announcement_id = ?",
            Long.class,
            announcementId
        );
        return count != null ? count : 0;
    }

    @Override
    public double averageRating(Long announcementId) {
        Double avg = jdbc.queryForObject(
            "SELECT AVG(rating) FROM announcement_reviews WHERE announcement_id = ?",
            Double.class,
            announcementId
        );
        return avg != null ? avg : 0.0;
    }

    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("r_id"));
        r.setAnnouncementId(rs.getLong("r_announcement_id"));
        r.setRating(rs.getInt("rating"));
        r.setCommento(rs.getString("commento"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        User u = new User();
        u.setId(rs.getLong("u_id"));
        u.setNome(rs.getString("u_nome"));
        u.setCognome(rs.getString("u_cognome"));
        u.setEmail(rs.getString("u_email"));
        u.setRuolo(rs.getString("u_ruolo"));

        r.setUser(u);
        return r;
    }
}
