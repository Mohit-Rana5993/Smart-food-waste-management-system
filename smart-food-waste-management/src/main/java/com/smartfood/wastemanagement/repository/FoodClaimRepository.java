package com.smartfood.wastemanagement.repository;

import com.smartfood.wastemanagement.entity.FoodClaim;
import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.entity.FoodDonation;
import com.smartfood.wastemanagement.enums.ClaimStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodClaimRepository extends JpaRepository<FoodClaim, Long> {

    List<FoodClaim> findByNgoOrderByCreatedAtDesc(AppUser ngo);

    @Query("""
        SELECT c FROM FoodClaim c
        JOIN FETCH c.donation d
        JOIN FETCH d.restaurant
        WHERE c.ngo = :ngo
        ORDER BY c.createdAt DESC
    """)
    List<FoodClaim> findByNgoWithDonationAndRestaurant(
            @Param("ngo") AppUser ngo
    );

    List<FoodClaim> findByDonationOrderByCreatedAtDesc(FoodDonation donation);

    boolean existsByDonationAndNgo(FoodDonation donation, AppUser ngo);

    boolean existsByDonationAndStatus(FoodDonation donation, ClaimStatus status);
}