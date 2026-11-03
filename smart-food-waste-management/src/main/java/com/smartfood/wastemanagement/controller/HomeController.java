package com.smartfood.wastemanagement.controller;

import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.entity.FoodDonation;
import com.smartfood.wastemanagement.enums.*;
import com.smartfood.wastemanagement.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    private final AppUserRepository users;
    private final FoodDonationRepository donations;
    private final AppNotificationRepository notifications;

    public HomeController(AppUserRepository u, FoodDonationRepository d, AppNotificationRepository n) {
        users = u;
        donations = d;
        notifications = n;
    }

    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("donations", donations.findByStatusOrderByCreatedAtDesc(DonationStatus.AVAILABLE));
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String denied() {
        return "access-denied";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model m) {
        AppUser u = users.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        m.addAttribute("user", u);
        m.addAttribute("unread", notifications.countByRecipientAndReadFlagFalse(u));
        m.addAttribute("notifications", notifications.findTop50ByRecipientOrderByCreatedAtDesc(u));
        if (u.getRole() == Role.ADMIN)
            return "redirect:/admin";
        if (u.getRole() == Role.RESTAURANT)
            return "redirect:/restaurant";
        return "redirect:/ngo";
    }
}