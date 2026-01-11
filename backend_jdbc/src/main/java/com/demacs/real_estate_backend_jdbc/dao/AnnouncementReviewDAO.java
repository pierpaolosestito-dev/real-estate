package com.demacs.real_estate_backend_jdbc.dao;

import com.demacs.real_estate_backend_jdbc.model.Review;

import java.util.List;
import java.util.Optional;

public interface AnnouncementReviewDAO {

    List<Review> findByAnnouncementId(Long announcementId);

    Optional<Review> findByAnnouncementIdAndUserId(Long announcementId, Long userId);

    Review saveOrUpdate(Review review);

    void deleteByAnnouncementIdAndUserId(Long announcementId, Long userId);

    void deleteByAnnouncementId(Long announcementId);

    long countByAnnouncementId(Long announcementId);

    double averageRating(Long announcementId);
}
