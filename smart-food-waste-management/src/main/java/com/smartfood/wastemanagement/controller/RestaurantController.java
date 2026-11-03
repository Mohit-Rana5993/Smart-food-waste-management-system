package com.smartfood.wastemanagement.controller;

import com.smartfood.wastemanagement.entity.*;
import com.smartfood.wastemanagement.enums.*;
import com.smartfood.wastemanagement.repository.*;
import com.smartfood.wastemanagement.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {
    private final AppUserRepository users;
    private final FoodDonationRepository donations;
    private final FoodClaimRepository claims;
    private final UploadService uploads;
    private final NotificationService notices;

    public RestaurantController(AppUserRepository u, FoodDonationRepository d, FoodClaimRepository c, UploadService up,
            NotificationService n) {
        users = u;
        donations = d;
        claims = c;
        uploads = up;
        notices = n;
    }

    @GetMapping
    public String dashboard(Authentication a, Model m) {
        AppUser u = users.findByEmailIgnoreCase(a.getName()).orElseThrow();
        m.addAttribute("user", u);
        m.addAttribute("donations", donations.findByRestaurantOrderByCreatedAtDesc(u));
        m.addAttribute("claims", claims.findAll().stream()
                .filter(c -> c.getDonation().getRestaurant().getId().equals(u.getId())).toList());
        return "restaurant-dashboard";
    }

    @GetMapping("/donations/new")
    public String newDonation(Model m) {
        m.addAttribute("foodTypes", FoodType.values());
        return "donation-form";
    }

    @PostMapping("/donations")
    public String create(Authentication a, @RequestParam String foodName, @RequestParam FoodType foodType,
            @RequestParam Double quantity, @RequestParam String unit,
            @RequestParam(required = false) String description, @RequestParam String pickupAddress,
            @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude,
            @RequestParam String expiresAt, @RequestParam(required = false) MultipartFile photo, RedirectAttributes ra)
            throws IOException {
        AppUser u = users.findByEmailIgnoreCase(a.getName()).orElseThrow();
        if (u.getVerificationStatus() != VerificationStatus.APPROVED) {
            ra.addFlashAttribute("error", "Your account must be approved before posting donations.");
            return "redirect:/restaurant";
        }
        LocalDateTime expiry = LocalDateTime.parse(expiresAt);
        if (!expiry.isAfter(LocalDateTime.now())) {
            ra.addFlashAttribute("error", "Expiry must be in the future.");
            return "redirect:/restaurant/donations/new";
        }
        FoodDonation d = new FoodDonation();
        d.setRestaurant(u);
        d.setFoodName(foodName);
        d.setFoodType(foodType);
        d.setQuantity(quantity);
        d.setUnit(unit);
        d.setDescription(description);
        d.setPickupAddress(pickupAddress);
        d.setLatitude(latitude);
        d.setLongitude(longitude);
        d.setExpiresAt(expiry);
        d.setPreparedAt(LocalDateTime.now());
        d.setPhotoPath(uploads.save(photo));
        d.setStatus(DonationStatus.AVAILABLE);
        donations.save(d);
        for (AppUser ngo : users.findByRoleAndVerificationStatus(Role.NGO, VerificationStatus.APPROVED)) {
            notices.notify(ngo,
                    "New food donation: " + foodName + " in " + (u.getCity() == null ? "your area" : u.getCity()));
        }
        ra.addFlashAttribute("success", "Donation posted successfully.");
        return "redirect:/restaurant";
    }

    @PostMapping("/claims/{id}/decision")
    public String decide(Authentication a, @PathVariable Long id, @RequestParam String decision,
            RedirectAttributes ra) {
        AppUser restaurant = users.findByEmailIgnoreCase(a.getName()).orElseThrow();
        FoodClaim claim = claims.findById(id).orElseThrow();
        if (!claim.getDonation().getRestaurant().getId().equals(restaurant.getId()))
            return "redirect:/access-denied";
        if (claim.getStatus() != ClaimStatus.REQUESTED) {
            ra.addFlashAttribute("error", "This claim has already been processed.");
            return "redirect:/restaurant";
        }
        if ("approve".equals(decision)) {
            if (claims.existsByDonationAndStatus(claim.getDonation(), ClaimStatus.APPROVED)) {
                ra.addFlashAttribute("error", "Another NGO claim is already approved.");
                return "redirect:/restaurant";
            }
            claim.setStatus(ClaimStatus.APPROVED);
            claim.getDonation().setStatus(DonationStatus.APPROVED);
            notices.notify(claim.getNgo(), "Your claim was approved for: " + claim.getDonation().getFoodName());
        } else {
            claim.setStatus(ClaimStatus.REJECTED);
            notices.notify(claim.getNgo(), "Your claim was declined for: " + claim.getDonation().getFoodName());
        }
        claims.save(claim);
        donations.save(claim.getDonation());
        ra.addFlashAttribute("success", "Claim updated.");
        return "redirect:/restaurant";
    }

    @PostMapping("/claims/{id}/complete")
    public String complete(Authentication a, @PathVariable Long id, RedirectAttributes ra) {
        AppUser restaurant = users.findByEmailIgnoreCase(a.getName()).orElseThrow();
        FoodClaim c = claims.findById(id).orElseThrow();
        if (!c.getDonation().getRestaurant().getId().equals(restaurant.getId()))
            return "redirect:/access-denied";
        if (c.getStatus() != ClaimStatus.PICKED_UP) {
            ra.addFlashAttribute("error", "NGO must mark pickup first.");
            return "redirect:/restaurant";
        }
        c.setStatus(ClaimStatus.COMPLETED);
        c.getDonation().setStatus(DonationStatus.COMPLETED);
        claims.save(c);
        donations.save(c.getDonation());
        notices.notify(c.getNgo(), "Pickup completed for: " + c.getDonation().getFoodName());
        ra.addFlashAttribute("success", "Pickup marked complete.");
        return "redirect:/restaurant";
    }
}