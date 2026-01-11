package com.demacs.real_estate_backend_jdbc.service;

import com.demacs.real_estate_backend_jdbc.dao.UserDAO;
import com.demacs.real_estate_backend_jdbc.model.Announcement;
import com.demacs.real_estate_backend_jdbc.model.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final AnnouncementService announcementService;
    private final ReviewService reviewService;
    private final LikeService likeService;

    public UserService(
            UserDAO userDAO,
            AnnouncementService announcementService,
            ReviewService reviewService,
            LikeService likeService
    ) {
        this.userDAO = userDAO;
        this.announcementService = announcementService;
        this.reviewService = reviewService;
        this.likeService = likeService;
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public User findById(Long id) {
        return userDAO.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmailIgnoreCase(email).orElse(null);
    }

    public User create(User user) {
        return userDAO.save(user);
    }

    public void update(User user) {
        userDAO.update(user);
    }

    @Transactional
    public void delete(Long userId) {
        if (!userDAO.existsById(userId)) {
            return;
        }

        List<Announcement> list = announcementService.findByVendor(userId);

        for (Announcement a : list) {
            Long id = a.getId();
            likeService.deleteByAnnouncement(id);
            reviewService.deleteByAnnouncement(id);
            announcementService.delete(id);
        }

        userDAO.deleteById(userId);
    }
}
