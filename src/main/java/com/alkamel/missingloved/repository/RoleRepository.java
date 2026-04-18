// RoleRepository.java
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
