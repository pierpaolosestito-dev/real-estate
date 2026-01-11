package com.demacs.real_estate_backend_jdbc.controller;

import com.demacs.real_estate_backend_jdbc.model.*;
import com.demacs.real_estate_backend_jdbc.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

@RestController
@RequestMapping("/api/announcements/{announcementId}/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementReviewController {

    private final ReviewService reviewService;
    private final AnnouncementService announcementService;
    private final UserService userService;

    public AnnouncementReviewController(
            ReviewService reviewService,
            AnnouncementService announcementService,
            UserService userService
    ) {
        this.reviewService = reviewService;
        this.announcementService = announcementService;
        this.userService = userService;
    }

    // ==================== LIST ====================

    @GetMapping
    public List<Review> getReviews(@PathVariable Long announcementId) {
        return reviewService.findByAnnouncement(announcementId);
    }

    // ==================== STATS ====================

    @GetMapping("/stats")
    public Map<String, Object> stats(@PathVariable Long announcementId) {
        long count = reviewService.count(announcementId);
        double avg = reviewService.average(announcementId);

        return Map.of(
            "count", count,
            "average", avg
        );
    }

    // ==================== ADD / UPDATE ====================

    @PostMapping("/{userId}")
    public ResponseEntity<?> addOrUpdate(
            @PathVariable Long announcementId,
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload
    ) {
        Announcement announcement = announcementService.findById(announcementId);
        if (announcement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        User user = userService.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        int rating = (int) payload.get("rating");
        String commento = (String) payload.getOrDefault("commento", "");

        Review r = new Review();
        r.setAnnouncementId(announcementId);
        r.setUser(user);
        r.setRating(rating);
        r.setCommento(commento);

        reviewService.addOrUpdate(r);

        return ResponseEntity.ok().build();
    }

    // ==================== DELETE (single user review) ====================

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(
            @PathVariable Long announcementId,
            @PathVariable Long userId
    ) {
        reviewService.delete(announcementId, userId);
        return ResponseEntity.noContent().build();
    }
}
