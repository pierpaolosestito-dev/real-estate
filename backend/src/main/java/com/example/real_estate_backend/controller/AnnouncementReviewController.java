package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.*;
import com.example.real_estate_backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/announcements/{announcementId}/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementReviewController {

    private final AnnouncementReviewRepository reviewRepo;
    private final AnnouncementRepository announcementRepo;
    private final UserRepository userRepo;

    public AnnouncementReviewController(
        AnnouncementReviewRepository reviewRepo,
        AnnouncementRepository announcementRepo,
        UserRepository userRepo
    ) {
        this.reviewRepo = reviewRepo;
        this.announcementRepo = announcementRepo;
        this.userRepo = userRepo;
    }

    /* ==================== GET LIST ==================== */

    @GetMapping
    public List<AnnouncementReview> getReviews(
        @PathVariable Long announcementId
    ) {
        return reviewRepo.findByAnnouncementId(announcementId);
    }

    /* ==================== STATS ==================== */

    @GetMapping("/stats")
    public Map<String, Object> getStats(
        @PathVariable Long announcementId
    ) {
        long count = reviewRepo.countByAnnouncementId(announcementId);
        Double avg = reviewRepo.averageRating(announcementId);

        return Map.of(
            "count", count,
            "average", avg != null ? avg : 0
        );
    }

    /* ==================== ADD / UPDATE ==================== */

    @PostMapping("/{userId}")
    public ResponseEntity<?> addOrUpdateReview(
        @PathVariable Long announcementId,
        @PathVariable Long userId,
        @RequestBody Map<String, Object> payload
    ) {
        int rating = (int) payload.get("rating");
        String commento = (String) payload.getOrDefault("commento", "");

        Announcement announcement = announcementRepo.findById(announcementId)
            .orElseThrow();

        User user = userRepo.findById(userId)
            .orElseThrow();

        AnnouncementReview review = reviewRepo
            .findByAnnouncementIdAndUserId(announcementId, userId)
            .orElse(new AnnouncementReview());

        review.setAnnouncement(announcement);
        review.setUser(user);
        review.setRating(rating);
        review.setCommento(commento);

        reviewRepo.save(review);
        return ResponseEntity.ok().build();
    }

    /* ==================== DELETE ==================== */

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteReview(
        @PathVariable Long announcementId,
        @PathVariable Long userId
    ) {
        reviewRepo.deleteByAnnouncementIdAndUserId(announcementId, userId);
        return ResponseEntity.noContent().build();
    }
}
