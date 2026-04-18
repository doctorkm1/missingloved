package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SightUsrRepository extends JpaRepository<User, Long> {
    User findByUserCode(String userCode);
}
