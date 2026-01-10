package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.User;
import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.repository.*;
import com.example.real_estate_backend.utils.SessionManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementLikeRepository likeRepository;
    private final AnnouncementReviewRepository reviewRepository;

    public UserController(
        UserRepository userRepository,
        AnnouncementRepository announcementRepository,
        AnnouncementLikeRepository likeRepository,
        AnnouncementReviewRepository reviewRepository
    ) {
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
        this.likeRepository = likeRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * GET /api/users
     * Ritorna tutti gli utenti
     */

@PostMapping("/login")
public User login(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String password = body.get("password");

    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Credenziali non valide"
        ));

    // nuova verifica password
    if (!password.equals(user.getPassword())) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Credenziali non valide"
        );
    }

    // salva "sessione" didattica
    SessionManager.getInstance().set(user.getId());

    return user;
}


@PostMapping("/logout")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void logout() {
    SessionManager.getInstance().clear();
}


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
        // password ignorata (come deciso)

        return userRepository.save(user);
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un utente e TUTTI i dati collegati
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
            );
        }

        // 1️⃣ recupera tutti gli annunci del venditore
        List<Announcement> announcements =
            announcementRepository.findByVenditoreId(id);

        // 2️⃣ elimina dipendenze di ogni annuncio
        for (Announcement a : announcements) {
            Long announcementId = a.getId();

            likeRepository.deleteByAnnouncementId(announcementId);
            reviewRepository.deleteByAnnouncementId(announcementId);
            announcementRepository.deleteById(announcementId);
        }

        // 3️⃣ elimina l’utente
        userRepository.deleteById(id);
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
