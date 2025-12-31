package com.example.real_estate_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "announcement_reviews",
    uniqueConstraints = @UniqueConstraint(columnNames = {"announcement_id", "user_id"})
)
public class AnnouncementReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many reviews -> one announcement
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    // Many reviews -> one user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating; // 1..5

    @Column(length = 2000)
    private String commento;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Announcement getAnnouncement() { return announcement; }
    public User getUser() { return user; }
    public Integer getRating() { return rating; }
    public String getCommento() { return commento; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAnnouncement(Announcement announcement) { this.announcement = announcement; }
    public void setUser(User user) { this.user = user; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setCommento(String commento) { this.commento = commento; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
