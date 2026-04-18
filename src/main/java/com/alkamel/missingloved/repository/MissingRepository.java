
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Missing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Integer> {
    int countByActivatedTrue();

    Missing findByPhotoFileName(String photoFileName);

    Page<Missing> findByActivatedTrue(Pageable pageable);

    List<Missing> findByActivatedTrueOrderByCreatedAtDesc();

    @Query("SELECT m FROM Missing m WHERE m.activated = true " +
           "AND (:gov IS NULL OR TRIM(LOWER(m.lastSeenGovernorate)) = TRIM(LOWER(:gov))) " +
           "AND (:city IS NULL OR TRIM(LOWER(m.lastSeenCity)) = TRIM(LOWER(:city))) " +
           "AND (:from IS NULL OR m.lastSeenDate >= :from) " +
           "AND (:to IS NULL OR m.lastSeenDate <= :to) " +
           "ORDER BY m.createdAt DESC")
    List<Missing> findFiltered(
            @Param("gov") String governorate,
            @Param("city") String city,
            @Param("from") LocalDate fromDate,
            @Param("to") LocalDate toDate
    );

    @Query("SELECT COUNT(m) FROM Missing m WHERE m.activated = true " +
           "AND (:gov IS NULL OR TRIM(LOWER(m.lastSeenGovernorate)) = TRIM(LOWER(:gov))) " +
           "AND (:city IS NULL OR TRIM(LOWER(m.lastSeenCity)) = TRIM(LOWER(:city))) " +
           "AND (:from IS NULL OR m.lastSeenDate >= :from) " +
           "AND (:to IS NULL OR m.lastSeenDate <= :to)")
    int countFiltered(
            @Param("gov") String governorate,
            @Param("city") String city,
            @Param("from") LocalDate fromDate,
            @Param("to") LocalDate toDate
    );
}
