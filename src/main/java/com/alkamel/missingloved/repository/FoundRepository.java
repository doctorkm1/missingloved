// FoundRepository.java
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Found;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundRepository extends JpaRepository<Found, Integer> {
}
