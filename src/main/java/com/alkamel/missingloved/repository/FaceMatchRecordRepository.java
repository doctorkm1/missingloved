package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.FaceMatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaceMatchRecordRepository extends JpaRepository<FaceMatchRecord, Long> {
    List<FaceMatchRecord> findByStatus(String status);
}
