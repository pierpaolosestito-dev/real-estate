package com.demacs.real_estate_backend_jdbc.dao;

import com.demacs.real_estate_backend_jdbc.model.Announcement;

import java.util.List;
import java.util.Optional;

public interface AnnouncementDAO {

    List<Announcement> findAll();

    Optional<Announcement> findById(Long id);

    List<Announcement> findByVenditoreId(Long venditoreId);

    Announcement save(Announcement announcement);

    void update(Announcement announcement);

    void deleteById(Long id);

    boolean existsById(Long id);
}
