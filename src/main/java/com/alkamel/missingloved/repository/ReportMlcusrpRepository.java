package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReportMlcusrpRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);  // ✅ Add this line
    boolean existsByNationalId(String nationalId);

    // Optional: You may add custom queries here if needed
}
