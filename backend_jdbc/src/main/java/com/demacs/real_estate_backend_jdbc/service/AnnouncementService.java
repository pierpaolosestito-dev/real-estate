package com.demacs.real_estate_backend_jdbc.service;

import com.demacs.real_estate_backend_jdbc.dao.AnnouncementDAO;
import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementDAO announcementDAO;
    private final UserDAO userDAO;
    private final ReviewService reviewService;
    private final LikeService likeService;

    public AnnouncementService(
            AnnouncementDAO announcementDAO,
            ReviewService reviewService,
            LikeService likeService,
            UserDAO userDAO
    ) {
        this.announcementDAO = announcementDAO;
        this.reviewService = reviewService;
        this.likeService = likeService;
        this.userDAO = userDAO;
    }

    public List<Announcement> findAll() {
        return announcementDAO.findAll();
    }

    public Announcement findById(Long id) {
        return announcementDAO.findById(id).orElse(null);
    }

    public List<Announcement> findByVendor(Long vendorId) {
        return announcementDAO.findByVenditoreId(vendorId);
    }

    @Transactional
    public Announcement create(Announcement a) {
        User venditore = userDAO.findById(a.getVenditore().getId()).orElseThrow();
        a.setVenditore(venditore);
        return announcementDAO.save(a);
    }

    @Transactional
    public void update(Announcement a) {
        announcementDAO.update(a);
    }

    @Transactional
    public void delete(Long id) {
        likeService.deleteByAnnouncement(id);
        reviewService.deleteByAnnouncement(id);
        announcementDAO.deleteById(id);
    }

    public boolean exists(Long id) {
        return announcementDAO.existsById(id);
    }
}
