package com.demacs.real_estate_backend_jdbc.controller;

import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.User;
import com.demacs.real_estate_backend_jdbc.dto.CreateAnnouncementRequest;
import com.demacs.real_estate_backend_jdbc.model.Property;
import com.demacs.real_estate_backend_jdbc.service.AnnouncementService;
import com.demacs.real_estate_backend_jdbc.service.UserService;
import com.demacs.real_estate_backend_jdbc.service.ReviewService;
import com.demacs.real_estate_backend_jdbc.service.LikeService;

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

    private final AnnouncementService announcementService;
    private final LikeService likeService;
    private final ReviewService reviewService;
    private final UserService userService;

    public AnnouncementController(
            AnnouncementService announcementService,
            LikeService likeService,
            ReviewService reviewService,
            UserService userService
    ) {
        this.announcementService = announcementService;
        this.likeService = likeService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // ===================== GET ALL =====================

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Announcement> getAll() {
        return announcementService.findAll();
    }

    // ===================== GET BY ID =====================

    @GetMapping("/{id}")
    public Announcement getById(@PathVariable Long id) {
        Announcement a = announcementService.findById(id);
        if (a == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        return a;
    }

    // ===================== GET BY VENDOR =====================

    @GetMapping("/vendor/{vendorId}")
    public List<Announcement> getByVendor(@PathVariable Long vendorId) {
        return announcementService.findByVendor(vendorId);
    }

    // ===================== CREATE =====================

    @PostMapping
    public ResponseEntity<Announcement> create(@RequestBody CreateAnnouncementRequest req) {

        // costruisci property
        Property property = new Property();
        property.setTipo(req.getImmobile().getTipo());
        property.setSuperficieMq(req.getImmobile().getSuperficieMq());
        property.setStanze(req.getImmobile().getStanze());
        property.setBagni(req.getImmobile().getBagni());
        property.setLocation(req.getImmobile().getLocation());

        // costruisci announcement
        Announcement announcement = new Announcement();
        announcement.setTitolo(req.getTitolo());
        announcement.setDescrizione(req.getDescrizione());
        announcement.setPrezzo(req.getPrezzo());
        announcement.setTipo(req.getTipo());
        announcement.setImageUrl(req.getImageUrl());
        announcement.setImmobile(property);
        announcement.setDataPubblicazione(LocalDate.now());

        // lookup venditore
        User venditore = userService.findById(req.getVenditoreId());
        if (venditore == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Venditore non valido");
        }

        announcement.setVenditore(venditore);

        Announcement saved = announcementService.create(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ===================== DELETE =====================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        Announcement existing = announcementService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        likeService.deleteByAnnouncement(id);
        reviewService.deleteByAnnouncement(id);
        announcementService.delete(id);
    }

    // ===================== UPDATE =====================

    @PutMapping("/{id}")
    @Transactional
    public Announcement update(
            @PathVariable Long id,
            @RequestBody Announcement updated
    ) {

        Announcement existing = announcementService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }

        // update dati annuncio
        existing.setTitolo(updated.getTitolo());
        existing.setDescrizione(updated.getDescrizione());
        existing.setPrezzo(updated.getPrezzo());
        existing.setTipo(updated.getTipo());
        existing.setImageUrl(updated.getImageUrl());

        // update immobili (1:1)
        if (existing.getImmobile() != null && updated.getImmobile() != null) {
            existing.getImmobile().setTipo(updated.getImmobile().getTipo());
            existing.getImmobile().setSuperficieMq(updated.getImmobile().getSuperficieMq());
            existing.getImmobile().setStanze(updated.getImmobile().getStanze());
            existing.getImmobile().setBagni(updated.getImmobile().getBagni());
            existing.getImmobile().setLocation(updated.getImmobile().getLocation());
        }

        announcementService.update(existing);
        return existing;
    }
}
