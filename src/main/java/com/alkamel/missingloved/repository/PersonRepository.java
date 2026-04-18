package com.alkamel.missingloved.repository;

import com.alkamel.missingloved.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // JpaRepository provides basic CRUD operations automatically
}
