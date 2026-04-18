// UserRoleRepository.java
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}

