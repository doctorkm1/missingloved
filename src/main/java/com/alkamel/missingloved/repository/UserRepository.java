
package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserCode(String userCode);

    Optional<User> findByEmail(String email);

    Optional<User> findByNationalId(String nationalId);


    boolean existsByUserCode(String userCode);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);
}
