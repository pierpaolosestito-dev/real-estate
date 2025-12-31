package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.model.AnnouncementLike;
import com.example.real_estate_backend.model.User;
import com.example.real_estate_backend.repository.AnnouncementLikeRepository;
import com.example.real_estate_backend.repository.AnnouncementRepository;
import com.example.real_estate_backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementLikeController {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AnnouncementLikeRepository likeRepository;

    public AnnouncementLikeController(
            AnnouncementRepository announcementRepository,
            UserRepository userRepository,
            AnnouncementLikeRepository likeRepository
    ) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * POST /api/announcements/{id}/like?userId=XX
     * Aggiunge un like (idempotente)
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        if (likeRepository.existsByAnnouncementIdAndUserId(id, userId)) {
            return ResponseEntity.noContent().build();
        }

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found")
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );

        AnnouncementLike like = new AnnouncementLike();
        like.setAnnouncement(announcement);
        like.setUser(user);

        likeRepository.save(like);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /api/announcements/{id}/like?userId=XX
     * Rimuove un like
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        likeRepository.deleteByAnnouncementIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/announcements/{id}/likes/count
     * Numero totale di like
     */
    @GetMapping("/{id}/likes/count")
    public long countLikes(@PathVariable Long id) {
        return likeRepository.countByAnnouncementId(id);
    }

    /**
     * GET /api/announcements/{id}/likes/me?userId=XX
     * L'utente ha messo like?
     */
    @GetMapping("/{id}/likes/me")
    public boolean likedByMe(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return likeRepository.existsByAnnouncementIdAndUserId(id, userId);
    }

    
}
