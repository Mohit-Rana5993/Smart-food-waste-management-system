package com.smartfood.wastemanagement.controller;

import com.smartfood.wastemanagement.entity.*;
import com.smartfood.wastemanagement.enums.*;
import com.smartfood.wastemanagement.repository.*;
import com.smartfood.wastemanagement.service.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ngo")
public class NgoController {

    private final AppUserRepository users;
    private final FoodDonationRepository donations;
    private final FoodClaimRepository claims;
    private final NotificationService notices;
    private final DistanceService distance;

    public NgoController(
            AppUserRepository u,
            FoodDonationRepository d,
            FoodClaimRepository c,
            NotificationService n,
            DistanceService ds) {

        users = u;
        donations = d;
        claims = c;
        notices = n;
        distance = ds;
    }

    @GetMapping
    public String dashboard(Authentication a, Model m) {

        AppUser ngo = users.findByEmailIgnoreCase(a.getName())
                .orElseThrow();

        m.addAttribute("user", ngo);

        // Updated query: fetch donation and restaurant together
        m.addAttribute(
                "claims",
                claims.findByNgoWithDonationAndRestaurant(ngo)
        );

        var available = donations
                .findByStatusInOrderByCreatedAtDesc(
                        List.of(
                                DonationStatus.AVAILABLE,
                                DonationStatus.REQUESTED
                        )
                )
                .stream()
                .filter(d -> d.getExpiresAt() == null
                        || d.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(d -> !claims.existsByDonationAndNgo(d, ngo))
                .sorted((x, y) ->
                        Double.compare(
                                distanceFor(x, ngo),
                                distanceFor(y, ngo)
                        ))
                .toList();

        m.addAttribute("donations", available);

        m.addAttribute(
                "distances",
                available.stream().collect(
                        Collectors.toMap(
                                FoodDonation::getId,
                                d -> distanceFor(d, ngo)
                        )
                )
        );

        return "ngo-dashboard";
    }

    private double distanceFor(FoodDonation d, AppUser n) {

        if (n.getLatitude() == null
                || n.getLongitude() == null
                || d.getLatitude() == null
                || d.getLongitude() == null) {
            return 99999;
        }

        return distance.km(
                n.getLatitude(),
                n.getLongitude(),
                d.getLatitude(),
                d.getLongitude()
        );
    }

    @PostMapping("/donations/{id}/claim")
    public String claim(
            Authentication a,
            @PathVariable Long id,
            RedirectAttributes ra) {

        AppUser ngo = users.findByEmailIgnoreCase(a.getName())
                .orElseThrow();

        if (ngo.getVerificationStatus() != VerificationStatus.APPROVED) {
            ra.addFlashAttribute(
                    "error",
                    "Your NGO account must be approved before claiming food."
            );
            return "redirect:/ngo";
        }

        FoodDonation d = donations.findById(id).orElseThrow();

        if (d.getStatus() != DonationStatus.AVAILABLE
                && d.getStatus() != DonationStatus.REQUESTED) {

            ra.addFlashAttribute(
                    "error",
                    "This donation is no longer available."
            );
            return "redirect:/ngo";
        }

        if (claims.existsByDonationAndNgo(d, ngo)) {
            ra.addFlashAttribute(
                    "error",
                    "You have already requested this donation."
            );
            return "redirect:/ngo";
        }

        FoodClaim c = new FoodClaim();
        c.setDonation(d);
        c.setNgo(ngo);

        claims.save(c);

        d.setStatus(DonationStatus.REQUESTED);
        donations.save(d);

        notices.notify(
                d.getRestaurant(),
                "An NGO requested your donation: " + d.getFoodName()
        );

        ra.addFlashAttribute(
                "success",
                "Claim request sent to restaurant."
        );

        return "redirect:/ngo";
    }

    @PostMapping("/claims/{id}/pickup")
    public String pickup(
            Authentication a,
            @PathVariable Long id,
            RedirectAttributes ra) {

        AppUser ngo = users.findByEmailIgnoreCase(a.getName())
                .orElseThrow();

        FoodClaim c = claims.findById(id).orElseThrow();

        if (!c.getNgo().getId().equals(ngo.getId())) {
            return "redirect:/access-denied";
        }

        if (c.getStatus() != ClaimStatus.APPROVED) {
            ra.addFlashAttribute(
                    "error",
                    "Only approved claims can be marked picked up."
            );
            return "redirect:/ngo";
        }

        c.setStatus(ClaimStatus.PICKED_UP);
        c.getDonation().setStatus(DonationStatus.PICKED_UP);

        claims.save(c);
        donations.save(c.getDonation());

        notices.notify(
                c.getDonation().getRestaurant(),
                "NGO marked pickup for: "
                        + c.getDonation().getFoodName()
                        + ". Confirm handover when done."
        );

        ra.addFlashAttribute(
                "success",
                "Pickup status updated."
        );

        return "redirect:/ngo";
    }
}