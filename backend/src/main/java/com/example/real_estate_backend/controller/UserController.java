package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.User;
import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.repository.UserRepository;
import com.example.real_estate_backend.repository.AnnouncementLikeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;
    private final AnnouncementLikeRepository likeRepository;

    public UserController(
            UserRepository userRepository,
            AnnouncementLikeRepository likeRepository
    ) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * GET /api/users
     * Ritorna tutti gli utenti
     */
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * POST /api/users
     * Crea un nuovo utente
     */
    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * PUT /api/users/{id}
     * Aggiorna i dati utente
     */
    @PutMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @RequestBody User updated
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        user.setNome(updated.getNome());
        user.setCognome(updated.getCognome());
        user.setEmail(updated.getEmail());
        user.setRuolo(updated.getRuolo());
        // password ignorata per ora

        return userRepository.save(user);
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un utente (delete fisico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users/{id}/liked-announcements
     * Ritorna la lista degli annunci a cui l'utente ha messo like
     */
    @GetMapping("/{id}/liked-announcements")
    public List<Announcement> likedAnnouncements(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        return likeRepository.findLikedAnnouncementsByUserId(id);
    }
}
