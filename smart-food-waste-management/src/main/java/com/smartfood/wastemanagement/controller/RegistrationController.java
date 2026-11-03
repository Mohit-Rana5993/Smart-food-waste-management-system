package com.smartfood.wastemanagement.controller;

import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.enums.*;
import com.smartfood.wastemanagement.repository.AppUserRepository;
import com.smartfood.wastemanagement.service.UploadService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final UploadService uploads;

    public RegistrationController(AppUserRepository u, PasswordEncoder e, UploadService up) {
        users = u;
        encoder = e;
        uploads = up;
    }

    @GetMapping
    public String choose() {
        return "register-choice";
    }

    @GetMapping("/restaurant")
    public String restaurant(Model m) {
        m.addAttribute("roleName", "Restaurant");
        m.addAttribute("rolePath", "restaurant");
        return "register";
    }

    @GetMapping("/ngo")
    public String ngo(Model m) {
        m.addAttribute("roleName", "NGO");
        m.addAttribute("rolePath", "ngo");
        return "register";
    }

    @PostMapping("/{role}")
    public String register(@PathVariable String role, @RequestParam String name, @RequestParam String email,
            @RequestParam String password, @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address, @RequestParam(required = false) String city,
            @RequestParam(required = false) String state, @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String registrationNumber, @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude, @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile document, RedirectAttributes ra, Model m) throws IOException {
        if (users.existsByEmailIgnoreCase(email)) {
            ra.addFlashAttribute("error", "An account with this email already exists.");
            return "redirect:/register/" + role;
        }
        if (password.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters.");
            return "redirect:/register/" + role;
        }
        try {
            AppUser u = new AppUser();
            u.setName(name.trim());
            u.setEmail(email.trim().toLowerCase());
            u.setPassword(encoder.encode(password));
            u.setRole(role.equals("restaurant") ? Role.RESTAURANT : Role.NGO);
            u.setPhone(phone);
            u.setAddress(address);
            u.setCity(city);
            u.setState(state);
            u.setPincode(pincode);
            u.setRegistrationNumber(registrationNumber);
            u.setLatitude(latitude);
            u.setLongitude(longitude);
            u.setPhotoPath(uploads.save(photo));
            u.setDocumentPath(uploads.save(document));
            u.setVerificationStatus(VerificationStatus.PENDING);
            users.save(u);
            ra.addFlashAttribute("success",
                    "Registration submitted. Please wait for admin verification before using all features.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register/" + role;
        }
    }
}