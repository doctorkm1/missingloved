// RecorderRepository.java
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Recorder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecorderRepository extends JpaRepository<Recorder, Integer> {
}
