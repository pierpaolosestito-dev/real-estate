package com.demacs.real_estate_backend_jdbc.service;

import com.demacs.real_estate_backend_jdbc.dao.*;
import com.demacs.real_estate_backend_jdbc.model.AnnouncementLike;
import com.demacs.real_estate_backend_jdbc.model.Announcement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {

    private final AnnouncementLikeDAO likeDAO;

    public LikeService(AnnouncementLikeDAO likeDAO) {
        this.likeDAO = likeDAO;
    }

    public boolean exists(Long announcementId, Long userId) {
        return likeDAO.existsByAnnouncementIdAndUserId(announcementId, userId);
    }

    public long count(Long announcementId) {
        return likeDAO.countByAnnouncementId(announcementId);
    }

    @Transactional
    public void like(AnnouncementLike like) {
        likeDAO.insert(like);
    }

    @Transactional
    public void unlike(Long announcementId, Long userId) {
        likeDAO.deleteByAnnouncementIdAndUserId(announcementId, userId);
    }

    // ⬇️ questo è quello che ci mancava
    @Transactional
    public void deleteByAnnouncement(Long announcementId) {
        likeDAO.deleteByAnnouncementId(announcementId);
    }

    public List<Announcement> likedBy(Long userId) {
        return likeDAO.findLikedAnnouncementsByUserId(userId);
    }
}
