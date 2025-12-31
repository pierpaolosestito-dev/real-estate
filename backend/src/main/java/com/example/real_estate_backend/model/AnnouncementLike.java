package com.example.real_estate_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "announcement_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"announcement_id", "user_id"})
)
public class AnnouncementLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many likes -> one announcement
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    // Many likes -> one user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Announcement getAnnouncement() { return announcement; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAnnouncement(Announcement announcement) { this.announcement = announcement; }
    public void setUser(User user) { this.user = user; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
