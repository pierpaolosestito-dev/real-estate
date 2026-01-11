package com.demacs.real_estate_backend_jdbc.service;

import com.demacs.real_estate_backend_jdbc.dao.AnnouncementDAO;
import com.demacs.real_estate_backend_jdbc.dao.AnnouncementReviewDAO;
import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import com.demacs.real_estate_backend_jdbc.model.Review;
import com.demacs.real_estate_backend_jdbc.model.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final AnnouncementReviewDAO reviewDAO;
    private final AnnouncementDAO announcementDAO;
    private final UserDAO userDAO;

    public ReviewService(AnnouncementReviewDAO reviewDAO, AnnouncementDAO announcementDAO, UserDAO userDAO) {
        this.reviewDAO = reviewDAO;
        this.announcementDAO = announcementDAO;
        this.userDAO = userDAO;
    }

    public List<Review> findByAnnouncement(Long id) {
        return reviewDAO.findByAnnouncementId(id);
    }

    @Transactional
    public Review addOrUpdate(Review r) {
        User u = userDAO.findById(r.getUser().getId()).orElseThrow();
        r.setUser(u);
        return reviewDAO.saveOrUpdate(r);
    }

    @Transactional
    public void delete(Long announcementId, Long userId) {
        reviewDAO.deleteByAnnouncementIdAndUserId(announcementId, userId);
    }

    // ⬇️ anche qui quello che serve per cascata
    @Transactional
    public void deleteByAnnouncement(Long announcementId) {
        reviewDAO.deleteByAnnouncementId(announcementId);
    }

    public long count(Long announcementId) {
        return reviewDAO.countByAnnouncementId(announcementId);
    }

    public double average(Long announcementId) {
        return reviewDAO.averageRating(announcementId);
    }
}
