package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.model.CreateAnnouncementRequest;
import com.example.real_estate_backend.model.Property;
import com.example.real_estate_backend.model.User;
import com.example.real_estate_backend.repository.AnnouncementRepository;
import com.example.real_estate_backend.repository.AnnouncementLikeRepository;
import com.example.real_estate_backend.repository.AnnouncementReviewRepository;
import com.example.real_estate_backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementLikeRepository announcementLikeRepository;
    private final AnnouncementReviewRepository announcementReviewRepository;
    private final UserRepository userRepository;

    public AnnouncementController(
        AnnouncementRepository announcementRepository,
        AnnouncementLikeRepository announcementLikeRepository,
        AnnouncementReviewRepository announcementReviewRepository,
        UserRepository userRepository
    ) {
        this.announcementRepository = announcementRepository;
        this.announcementLikeRepository = announcementLikeRepository;
        this.announcementReviewRepository = announcementReviewRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET ALL
    // =====================================================
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Announcement> getAll() {
        return announcementRepository.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @GetMapping("/{id}")
    public Announcement getById(@PathVariable Long id) {
        return announcementRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Announcement not found"
            ));
    }

    // =====================================================
    // GET BY VENDOR
    // =====================================================
    @GetMapping("/vendor/{vendorId}")
    public List<Announcement> getByVendor(@PathVariable Long vendorId) {
        return announcementRepository.findByVenditoreId(vendorId);
    }

    // =====================================================
    // CREATE
    // =====================================================
    @PostMapping
    public ResponseEntity<Announcement> create(
        @RequestBody CreateAnnouncementRequest req
    ) {

        // 1️⃣ PROPERTY
        Property property = new Property();
        property.setTipo(req.getImmobile().getTipo());
        property.setSuperficieMq(req.getImmobile().getSuperficieMq());
        property.setStanze(req.getImmobile().getStanze());
        property.setBagni(req.getImmobile().getBagni());
        property.setLocation(req.getImmobile().getLocation());

        // 2️⃣ ANNOUNCEMENT
        Announcement announcement = new Announcement();
        announcement.setTitolo(req.getTitolo());
        announcement.setDescrizione(req.getDescrizione());
        announcement.setPrezzo(req.getPrezzo());
        announcement.setTipo(req.getTipo());
        announcement.setImageUrl(req.getImageUrl());
        announcement.setImmobile(property);
        announcement.setDataPubblicazione(LocalDate.now());

        // 3️⃣ VENDITORE (recuperato da DB)
        User venditore = userRepository.findById(req.getVenditoreId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Venditore non valido"
            ));
        announcement.setVenditore(venditore);

        // 4️⃣ SAVE (Cascade salva Property)
        Announcement saved = announcementRepository.save(announcement);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(saved);
    }

    // =====================================================
    // DELETE (LIKES + REVIEWS + ANNOUNCEMENT)
    // =====================================================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {

        if (!announcementRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Announcement not found"
            );
        }

        // 1️⃣ elimina likes
        announcementLikeRepository.deleteByAnnouncementId(id);

        // 2️⃣ elimina reviews
        announcementReviewRepository.deleteByAnnouncementId(id);

        // 3️⃣ elimina annuncio
        announcementRepository.deleteById(id);
    }

    @PutMapping("/{id}")
@Transactional
public Announcement update(
    @PathVariable Long id,
    @RequestBody Announcement updated
) {

    Announcement existing = announcementRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Announcement not found"
        ));

    // =====================
    // CAMPI ANNUNCIO
    // =====================
    existing.setTitolo(updated.getTitolo());
    existing.setDescrizione(updated.getDescrizione());
    existing.setPrezzo(updated.getPrezzo());
    existing.setTipo(updated.getTipo());
    existing.setImageUrl(updated.getImageUrl());

    // =====================
    // CAMPI IMMOBILE
    // =====================
    if (existing.getImmobile() != null && updated.getImmobile() != null) {
        existing.getImmobile().setTipo(updated.getImmobile().getTipo());
        existing.getImmobile().setSuperficieMq(updated.getImmobile().getSuperficieMq());
        existing.getImmobile().setStanze(updated.getImmobile().getStanze());
        existing.getImmobile().setBagni(updated.getImmobile().getBagni());
        existing.getImmobile().setLocation(updated.getImmobile().getLocation());
    }

    // JPA aggiorna automaticamente al commit
    return existing;
}

}
