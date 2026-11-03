package com.smartfood.wastemanagement.scheduler;

import com.smartfood.wastemanagement.entity.FoodDonation;
import com.smartfood.wastemanagement.enums.DonationStatus;
import com.smartfood.wastemanagement.repository.FoodDonationRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@EnableScheduling
public class DonationExpiryScheduler {
    private final FoodDonationRepository repo;

    public DonationExpiryScheduler(FoodDonationRepository r) {
        repo = r;
    }

    @Scheduled(fixedDelay = 60000)
    public void expire() {
        for (FoodDonation d : repo.findAll()) {
            if (d.getExpiresAt() != null && d.getExpiresAt().isBefore(LocalDateTime.now())
                    && (d.getStatus() == DonationStatus.AVAILABLE || d.getStatus() == DonationStatus.REQUESTED)) {
                d.setStatus(DonationStatus.EXPIRED);
                repo.save(d);
            }
        }
    }
}
