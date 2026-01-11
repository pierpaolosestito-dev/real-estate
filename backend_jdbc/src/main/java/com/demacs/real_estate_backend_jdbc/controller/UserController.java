package com.demacs.real_estate_backend_jdbc.controller;

import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.User;
import com.demacs.real_estate_backend_jdbc.service.UserService;
import com.demacs.real_estate_backend_jdbc.service.AnnouncementService;
import com.demacs.real_estate_backend_jdbc.service.ReviewService;
import com.demacs.real_estate_backend_jdbc.service.LikeService;
import com.demacs.real_estate_backend_jdbc.utils.SessionManager;

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

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final ReviewService reviewService;
    private final LikeService likeService;

    public UserController(
            UserService userService,
            AnnouncementService announcementService,
            ReviewService reviewService,
            LikeService likeService
    ) {
        this.userService = userService;
        this.announcementService = announcementService;
        this.reviewService = reviewService;
        this.likeService = likeService;
    }

    // ============================ LOGIN ============================

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide");
        }

        // didattico = file sessione
        SessionManager.getInstance().set(user.getId());

        return user;
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        SessionManager.getInstance().clear();
    }

    // ============================ CRUD UTENTI ============================

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User u) {
        return userService.create(u);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User updated) {
        User existing = userService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        existing.setNome(updated.getNome());
        existing.setCognome(updated.getCognome());
        existing.setEmail(updated.getEmail());
        existing.setRuolo(updated.getRuolo());
        // password NON aggiornata (fedeltà JPA)

        userService.update(existing);

        return existing;
    }

    // ============================ DELETE + CASCATA ============================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {

        User existing = userService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // 1) recupera annunci del venditore
        List<Announcement> list = announcementService.findByVendor(id);

        // 2) elimina dipendenze annuncio → like → review → annuncio
        for (Announcement a : list) {
            Long annId = a.getId();
            likeService.deleteByAnnouncement(annId);
            reviewService.deleteByAnnouncement(annId);
            announcementService.delete(annId);
        }

        // 3) elimina user
        userService.delete(id);
    }

    // ============================ ANNUNCI LIKATI ============================

    @GetMapping("/{id}/liked-announcements")
    public List<Announcement> liked(@PathVariable Long id) {

        User existing = userService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return likeService.likedBy(id);
    }
}
