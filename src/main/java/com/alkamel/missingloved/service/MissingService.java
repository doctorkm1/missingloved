package com.alkamel.missingloved.service;

import com.alkamel.missingloved.dto.MissingDTO;
import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.repository.MissingRepository;
import com.alkamel.missingloved.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class MissingService {

    @Autowired
    private MissingRepository missingRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new missing record (used by case-entry)
    public Missing createMissingRecord(MissingDTO missingDTO, String photoFileName) {
        Missing missing = new Missing();
        missing.setFirstName(missingDTO.getFirstName());
        missing.setSecondName(missingDTO.getSecondName());
        missing.setFamilyName(missingDTO.getFamilyName());
        missing.setGender(missingDTO.getGender());
        missing.setNationality(missingDTO.getNationality());
        missing.setNationalId(missingDTO.getNationalId());
        missing.setDob(missingDTO.getDob());
        missing.setCountry(missingDTO.getCountry());
        missing.setGovernment(missingDTO.getGovernment());
        missing.setCity(missingDTO.getCity());
        missing.setAddress(missingDTO.getAddress());
        missing.setMobile(missingDTO.getMobile());

        missing.setPhotoFileName(photoFileName);
        missing.setCreatedAt(LocalDateTime.now());
        missing.setActivated(false);

        return missingRepository.save(missing);
    }

    // Backfill utility (kept for consistency)
    public void backfillPhotoFileNames() {
        List<Missing> all = missingRepository.findAll();

        for (Missing m : all) {
            if (m.getPhotoUrl() != null && (m.getPhotoFileName() == null || m.getPhotoFileName().isBlank())) {
                String[] parts = m.getPhotoUrl().replace("\\", "/").split("/");
                String filename = parts[parts.length - 1];
                m.setPhotoFileName(filename);
                missingRepository.save(m);
                System.out.println("Updated photoFileName for case: " + m.getCaseCode() + " → " + filename);
            }
        }
        System.out.println("✅ Backfill complete.");
    }

    // Activate a missing record (set user who activated it)
// Activate a missing record (set user who activated it)
    public void activate(Integer id, Integer userId, String ip) {
        Missing missing = missingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Missing record not found"));

        missing.setActivated(true);
        missing.setActivatedAt(LocalDateTime.now());
        missing.setActivatedIp(ip);

        // Fetch the logged-in user by ID and set as activator
        if (userId != null) {
            userRepository.findById(userId.longValue())
                    .ifPresent(missing::setActivatedBy);
        } else {
            missing.setActivatedBy(null); // explicitly clear if no user
        }

        missingRepository.save(missing);
    }

    // Deactivate a missing record (set user who deactivated it)
    public void deactivate(Integer id, Integer userId, String ip) {
        Missing missing = missingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Missing record not found"));

        missing.setActivated(false);
        missing.setDeactivatedAt(LocalDateTime.now());
        missing.setDeactivatedIp(ip);

        // Fetch the logged-in user by ID and set as deactivator
        if (userId != null) {
            userRepository.findById(userId.longValue())
                    .ifPresent(missing::setDeactivatedBy);
        } else {
            missing.setDeactivatedBy(null);
        }

        missingRepository.save(missing);
    }

    // Delete a missing record
    public void delete(Integer id) {
        missingRepository.deleteById(id);
    }
}
