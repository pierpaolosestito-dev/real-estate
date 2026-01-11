package com.demacs.real_estate_backend_jdbc.dao.impl;

import com.demacs.real_estate_backend_jdbc.dao.AnnouncementLikeDAO;
import com.demacs.real_estate_backend_jdbc.dao.AnnouncementDAO;
import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.AnnouncementLike;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AnnouncementLikeDAOImpl implements AnnouncementLikeDAO {

    private final JdbcTemplate jdbc;
    private final AnnouncementDAO announcementDAO;

    public AnnouncementLikeDAOImpl(JdbcTemplate jdbc, AnnouncementDAO announcementDAO) {
        this.jdbc = jdbc;
        this.announcementDAO = announcementDAO;
    }

    @Override
    public boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId) {
        Long count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM announcement_likes WHERE announcement_id = ? AND user_id = ?",
            Long.class,
            announcementId, userId
        );
        return count != null && count > 0;
    }

    @Override
    public long countByAnnouncementId(Long announcementId) {
        Long count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM announcement_likes WHERE announcement_id = ?",
            Long.class,
            announcementId
        );
        return count != null ? count : 0;
    }

    @Override
    public void insert(AnnouncementLike like) {
        String sql = """
            INSERT INTO announcement_likes (
                announcement_id,
                user_id,
                created_at
            ) VALUES (?, ?, ?)
        """;

        jdbc.update(
            sql,
            like.getAnnouncementId(),
            like.getUserId(),
            like.getCreatedAt() != null ? Timestamp.valueOf(like.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now())
        );
    }

    @Override
    public void deleteByAnnouncementIdAndUserId(Long announcementId, Long userId) {
        jdbc.update(
            "DELETE FROM announcement_likes WHERE announcement_id = ? AND user_id = ?",
            announcementId, userId
        );
    }

    @Override
    public void deleteByAnnouncementId(Long announcementId) {
        jdbc.update(
            "DELETE FROM announcement_likes WHERE announcement_id = ?",
            announcementId
        );
    }

    @Override
    public List<Announcement> findLikedAnnouncementsByUserId(Long userId) {
        String sql = """
            SELECT a.id
            FROM announcement_likes al
            JOIN announcements a ON al.announcement_id = a.id
            WHERE al.user_id = ?
        """;

        return jdbc.query(
            sql,
            (rs, rowNum) -> announcementDAO.findById(rs.getLong("id")).orElse(null),
            userId
        ).stream().filter(a -> a != null).toList();
    }
}
