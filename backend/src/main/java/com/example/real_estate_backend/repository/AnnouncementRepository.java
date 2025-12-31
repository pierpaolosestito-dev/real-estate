package com.example.real_estate_backend.repository;

import com.example.real_estate_backend.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByVenditoreId(Long venditoreId);
}
