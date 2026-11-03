package com.smartfood.wastemanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class AppNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private AppUser recipient;
    @Column(nullable = false, length = 500)
    private String message;
    private boolean readFlag = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public AppUser getRecipient() {
        return recipient;
    }

    public void setRecipient(AppUser v) {
        recipient = v;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String v) {
        message = v;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean v) {
        readFlag = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}