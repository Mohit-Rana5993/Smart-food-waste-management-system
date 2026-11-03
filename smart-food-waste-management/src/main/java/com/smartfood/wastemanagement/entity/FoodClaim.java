package com.smartfood.wastemanagement.entity;

import com.smartfood.wastemanagement.enums.ClaimStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "food_claims",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"donation_id", "ngo_id"}
    )
)
public class FoodClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donation_id")
    private FoodDonation donation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ngo_id")
    private AppUser ngo;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.REQUESTED;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public FoodDonation getDonation() {
        return donation;
    }

    public void setDonation(FoodDonation v) {
        donation = v;
    }

    public AppUser getNgo() {
        return ngo;
    }

    public void setNgo(AppUser v) {
        ngo = v;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus v) {
        status = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
