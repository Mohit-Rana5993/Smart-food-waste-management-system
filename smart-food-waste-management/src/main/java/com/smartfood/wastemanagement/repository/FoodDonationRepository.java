package com.smartfood.wastemanagement.repository;
import org.springframework.data.jpa.repository.Query;
import com.smartfood.wastemanagement.entity.FoodDonation;
import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {
    List<FoodDonation> findByStatusOrderByCreatedAtDesc(DonationStatus status);

    List<FoodDonation> findByStatusInOrderByCreatedAtDesc(java.util.Collection<DonationStatus> statuses);

    List<FoodDonation> findByRestaurantOrderByCreatedAtDesc(AppUser restaurant);

    long countByStatus(DonationStatus status);
    @Query("SELECT d FROM FoodDonation d LEFT JOIN FETCH d.restaurant")
List<FoodDonation> findAllWithRestaurant();
}