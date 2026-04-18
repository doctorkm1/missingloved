// UserLogRepository.java
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {
}
