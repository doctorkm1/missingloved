package com.alkamel.missingloved.service;

import com.alkamel.missingloved.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user) throws Exception;

    Optional<User> getUserById(Long id);

    Optional<User> getUserByCode(String userCode);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    User findByUserCode(String userCode);

    // New methods for validation support
    boolean existsByUserCode(String userCode);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);
}
