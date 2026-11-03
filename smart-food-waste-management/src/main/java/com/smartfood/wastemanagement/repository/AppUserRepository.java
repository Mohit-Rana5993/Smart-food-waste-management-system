package com.smartfood.wastemanagement.repository;

import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.enums.Role;
import com.smartfood.wastemanagement.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<AppUser> findByRoleAndVerificationStatus(Role role, VerificationStatus status);

    List<AppUser> findByRole(Role role);
}