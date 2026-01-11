package com.demacs.real_estate_backend_jdbc.controller;

import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.AnnouncementLike;
import com.demacs.real_estate_backend_jdbc.model.User;
import com.demacs.real_estate_backend_jdbc.service.AnnouncementService;
import com.demacs.real_estate_backend_jdbc.service.LikeService;
import com.demacs.real_estate_backend_jdbc.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementLikeController {

    private final AnnouncementService announcementService;
    private final UserService userService;
    private final LikeService likeService;

    public AnnouncementLikeController(
            AnnouncementService announcementService,
            UserService userService,
            LikeService likeService
    ) {
        this.announcementService = announcementService;
        this.userService = userService;
        this.likeService = likeService;
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
        Announcement a = announcementService.findById(id);
        if (a == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        User u = userService.findById(userId);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // idempotenza
        if (likeService.exists(id, userId)) {
            return ResponseEntity.noContent().build();
        }

        AnnouncementLike like = new AnnouncementLike();
like.setAnnouncementId(id);
like.setUserId(userId);
likeService.like(like);


        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /api/announcements/{id}/like?userId=XX
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        // anche se non esiste, nel JPA originale risponde comunque 204
        likeService.unlike(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/announcements/{id}/likes/count
     */
    @GetMapping("/{id}/likes/count")
    public long countLikes(@PathVariable Long id) {
        return likeService.count(id);
    }

    /**
     * GET /api/announcements/{id}/likes/me?userId=XX
     */
    @GetMapping("/{id}/likes/me")
    public boolean likedByMe(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return likeService.exists(id, userId);
    }
}
