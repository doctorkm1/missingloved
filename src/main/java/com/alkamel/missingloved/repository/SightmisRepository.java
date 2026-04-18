package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Sightmis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SightmisRepository extends JpaRepository<Sightmis, Long> {

    // ✅ Native query to validate if case_code exists in the 'missing' table
    @Query(value = "SELECT EXISTS (SELECT 1 FROM missing WHERE case_code = :code)", nativeQuery = true)
    Long checkCaseCodeExists(@Param("code") String code);
}
