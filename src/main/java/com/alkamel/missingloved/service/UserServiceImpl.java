package com.alkamel.missingloved.service;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional  // ✅ ensures the save commits properly
    public User createUser(User user) throws Exception {
        User saved = userRepository.save(user);
        System.out.println("✅ [createUser] User saved: " + saved.getUserCode() + ", " + saved.getEmail());
        return saved;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByCode(String userCode) {
        return userRepository.findByUserCode(userCode);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional  // ✅ important for updating inside a POST request
    public User updateUser(Long id, User updatedUser) {
        Optional<User> optionalExisting = userRepository.findById(id);
        if (optionalExisting.isEmpty()) return null;

        User existing = optionalExisting.get();

        // ✅ Copy all fields to update safely
        existing.setFirstName(updatedUser.getFirstName());
        existing.setSecondName(updatedUser.getSecondName());
        existing.setFamilyName(updatedUser.getFamilyName());
        existing.setSex(updatedUser.getSex());
/*        existing.setMaritalStatus(updatedUser.getMaritalStatus());   */
        existing.setNationality(updatedUser.getNationality());
        existing.setNationalId(updatedUser.getNationalId());
        existing.setBirthDate(updatedUser.getBirthDate());
        existing.setLandline(updatedUser.getLandline());
        existing.setMobile(updatedUser.getMobile());
        existing.setCountry(updatedUser.getCountry());
        existing.setGovernment(updatedUser.getGovernment());
        existing.setCity(updatedUser.getCity());
        existing.setAddress(updatedUser.getAddress());
        existing.setEmail(updatedUser.getEmail());
        existing.setUserCode(updatedUser.getUserCode());
        existing.setPassword(updatedUser.getPassword());
        existing.setAuthorityCode(updatedUser.getAuthorityCode());
        existing.setActivated(updatedUser.isActivated());

        // Audit fields
        existing.setCreatedBy(updatedUser.getCreatedBy());
        existing.setCreatedAt(updatedUser.getCreatedAt());
        existing.setcreatedIp(updatedUser.getCreatedIp());

        existing.setActivatedBy(updatedUser.getActivatedBy());
        existing.setActivatedAt(updatedUser.getActivatedAt());
        existing.setActivationIp(updatedUser.getActivationIp());

        existing.setDeactivatedBy(updatedUser.getDeactivatedBy());
        existing.setDeactivatedAt(updatedUser.getDeactivatedAt());
        existing.setDeactivationIp(updatedUser.getDeactivationIp());

        User saved = userRepository.save(existing);
        System.out.println("✅ [updateUser] Updated: " + saved.getUserCode());
        return saved;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode).orElse(null);
    }

    // ✅ Helper methods for form validation
    @Override
    public boolean existsByUserCode(String userCode) {
        return userRepository.existsByUserCode(userCode);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return userRepository.existsByNationalId(nationalId);
    }
}
