package com.smartfood.wastemanagement.repository;

import com.smartfood.wastemanagement.entity.AppNotification;
import com.smartfood.wastemanagement.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findTop50ByRecipientOrderByCreatedAtDesc(AppUser recipient);

    long countByRecipientAndReadFlagFalse(AppUser recipient);
}