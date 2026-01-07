package com.example.real_estate_backend.repository;

import com.example.real_estate_backend.model.AnnouncementReview;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

public interface AnnouncementReviewRepository
        extends JpaRepository<AnnouncementReview, Long> {

    @Query("""
        select r
        from AnnouncementReview r
        join fetch r.user
        where r.announcement.id = :announcementId
        order by r.createdAt desc
    """)
    List<AnnouncementReview> findByAnnouncementId(
        @Param("announcementId") Long announcementId
    );

    Optional<AnnouncementReview> findByAnnouncementIdAndUserId(
        Long announcementId,
        Long userId
    );

    @Transactional
    void deleteByAnnouncementIdAndUserId(
        Long announcementId,
        Long userId
    );

    long countByAnnouncementId(Long announcementId);

    @Query("""
        select avg(r.rating)
        from AnnouncementReview r
        where r.announcement.id = :announcementId
    """)
    Double averageRating(@Param("announcementId") Long announcementId);

@Transactional
void deleteByAnnouncementId(Long announcementId);

}
