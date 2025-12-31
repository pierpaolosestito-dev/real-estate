package com.example.real_estate_backend.controller;

import com.example.real_estate_backend.model.Announcement;
import com.example.real_estate_backend.repository.AnnouncementRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:4200")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /** GET ALL */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Announcement>> getAll() {
        return ResponseEntity.ok(
            List.copyOf(announcementRepository.findAll())
        );
    }

    /** GET BY ID */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Announcement not found"
            ));

        return ResponseEntity.ok(announcement);
    }

    /** GET BY VENDOR */
    @GetMapping(value = "/vendor/{vendorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Announcement>> getByVendor(
        @PathVariable Long vendorId
    ) {
        return ResponseEntity.ok(
            announcementRepository.findByVenditoreId(vendorId)
        );
    }

    @PostMapping
public Announcement create(@RequestBody Announcement announcement) {
    return announcementRepository.save(announcement);
}



    /** UPDATE (MODIFICA) */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Announcement> update(
        @PathVariable Long id,
        @RequestBody Announcement updated
    ) {
        Announcement existing = announcementRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Announcement not found"
            ));

        // ⚠️ manteniamo l'id corretto
        updated.setId(existing.getId());

        Announcement saved = announcementRepository.save(updated);
        return ResponseEntity.ok(saved);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Announcement not found"
            );
        }
        announcementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
