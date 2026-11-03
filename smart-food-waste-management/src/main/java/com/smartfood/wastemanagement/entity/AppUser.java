package com.smartfood.wastemanagement.entity;

import com.smartfood.wastemanagement.enums.Role;
import com.smartfood.wastemanagement.enums.VerificationStatus;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String registrationNumber;
    private String photoPath;
    private String documentPath;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    private boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public AppUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String v) {
        password = v;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role v) {
        role = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        phone = v;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String v) {
        address = v;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String v) {
        city = v;
    }

    public String getState() {
        return state;
    }

    public void setState(String v) {
        state = v;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String v) {
        pincode = v;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String v) {
        registrationNumber = v;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String v) {
        photoPath = v;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String v) {
        documentPath = v;
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

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus v) {
        verificationStatus = v;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean v) {
        enabled = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}