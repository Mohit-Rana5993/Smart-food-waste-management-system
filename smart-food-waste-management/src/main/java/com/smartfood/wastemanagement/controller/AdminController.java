package com.smartfood.wastemanagement.controller;

import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.enums.*;
import com.smartfood.wastemanagement.repository.*;
import com.smartfood.wastemanagement.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AppUserRepository users;
    private final FoodDonationRepository donations;
    private final FoodClaimRepository claims;
    private final NotificationService notices;

    public AdminController(AppUserRepository u, FoodDonationRepository d, FoodClaimRepository c,
            NotificationService n) {
        users = u;
        donations = d;
        claims = c;
        notices = n;
    }

    @GetMapping
    public String dashboard(Model m) {
        m.addAttribute("restaurantCount", users.findByRole(Role.RESTAURANT).size());
        m.addAttribute("ngoCount", users.findByRole(Role.NGO).size());
        m.addAttribute("donationCount", donations.count());
        m.addAttribute("completedCount", donations.countByStatus(DonationStatus.COMPLETED));
        m.addAttribute("pendingUsers",
                users.findAll().stream().filter(
                        u -> u.getVerificationStatus() == VerificationStatus.PENDING && u.getRole() != Role.ADMIN)
                        .toList());
       m.addAttribute("donations", donations.findAllWithRestaurant());
        return "admin-dashboard";
    }

    @PostMapping("/users/{id}/verify")
    public String verify(@PathVariable Long id, @RequestParam VerificationStatus status, RedirectAttributes ra) {
        AppUser u = users.findById(id).orElseThrow();
        if (u.getRole() == Role.ADMIN) {
            ra.addFlashAttribute("error", "Admin accounts cannot be verified here.");
            return "redirect:/admin";
        }
        u.setVerificationStatus(status);
        users.save(u);
        notices.notify(u, "Your account verification status is now: " + status);
        ra.addFlashAttribute("success", "Account status updated.");
        return "redirect:/admin";
    }
}