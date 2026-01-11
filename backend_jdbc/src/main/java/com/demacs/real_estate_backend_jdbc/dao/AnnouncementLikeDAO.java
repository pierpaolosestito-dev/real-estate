package com.demacs.real_estate_backend_jdbc.dao;

import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.AnnouncementLike;

import java.util.List;

public interface AnnouncementLikeDAO {

    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);

    long countByAnnouncementId(Long announcementId);

    void insert(AnnouncementLike like);

    void deleteByAnnouncementIdAndUserId(Long announcementId, Long userId);

    void deleteByAnnouncementId(Long announcementId);

    List<Announcement> findLikedAnnouncementsByUserId(Long userId);
}
