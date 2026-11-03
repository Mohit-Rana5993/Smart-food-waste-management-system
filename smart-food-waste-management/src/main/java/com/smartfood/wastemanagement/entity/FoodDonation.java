package com.smartfood.wastemanagement.entity;

import com.smartfood.wastemanagement.enums.DonationStatus;
import com.smartfood.wastemanagement.enums.FoodType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_donations")
public class FoodDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AppUser restaurant;
    @Column(nullable = false)
    private String foodName;
    @Enumerated(EnumType.STRING)
    private FoodType foodType;
    @Column(nullable = false)
    private Double quantity;
    private String unit;
    @Column(length = 1200)
    private String description;
    private String photoPath;
    private LocalDateTime preparedAt;
    private LocalDateTime expiresAt;
    private String pickupAddress;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.AVAILABLE;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public AppUser getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(AppUser v) {
        restaurant = v;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String v) {
        foodName = v;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType v) {
        foodType = v;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double v) {
        quantity = v;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String v) {
        unit = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String v) {
        photoPath = v;
    }

    public LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(LocalDateTime v) {
        preparedAt = v;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime v) {
        expiresAt = v;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String v) {
        pickupAddress = v;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double v) {
        latitude = v;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double v) {
        longitude = v;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus v) {
        status = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}