package com.example.real_estate_backend.repository;

import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.model.AnnouncementLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;



public interface AnnouncementLikeRepository extends JpaRepository<AnnouncementLike, Long> {

    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);

    long countByAnnouncementId(Long announcementId);

    @Modifying
    @Transactional
    void deleteByAnnouncementIdAndUserId(Long announcementId, Long userId);

    @Query("""
    select al.announcement
    from AnnouncementLike al
    where al.user.id = :userId
""")
List<Announcement> findLikedAnnouncementsByUserId(Long userId);
}
