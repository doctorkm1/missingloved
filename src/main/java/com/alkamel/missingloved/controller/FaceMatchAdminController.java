package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.FaceMatchRecord;
import com.alkamel.missingloved.repository.FaceMatchRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin/face-match")
public class FaceMatchAdminController {

    private final FaceMatchRecordRepository faceMatchRepo;
    @Autowired
    private FaceMatchRecordRepository faceMatchRecordRepository;

    public FaceMatchAdminController(FaceMatchRecordRepository faceMatchRepo) {
        this.faceMatchRepo = faceMatchRepo;
    }

    // ✅ Show all match records
    @GetMapping("/list")
    public String listMatches(Model model) {
        List<FaceMatchRecord> matches = faceMatchRepo.findAll();

        List<Map<String, Object>> matchViews = new ArrayList<>();
        for (FaceMatchRecord match : matches) {
            Map<String, Object> view = new HashMap<>();
            view.put("match", match);

            // ✅ Calculate missing age
            if (match.getMissing() != null && match.getMissing().getDob() != null && match.getMatchDate() != null) {
                int age = java.time.Period.between(
                        match.getMissing().getDob(),
                        match.getMatchDate().toLocalDate()
                ).getYears();
                view.put("missingAge", age);
            } else {
                view.put("missingAge", "---");
            }

            matchViews.add(view);
        }

        model.addAttribute("matchViews", matchViews);
        return "match-list";
    }
    @PostMapping("/match/updateStatus")
    public String updateMatchStatus(@RequestParam("id") Long id,
                                    @RequestParam("newStatus") String newStatus,
                                    HttpSession session,
                                    HttpServletRequest request) {

        Optional<FaceMatchRecord> optional = faceMatchRecordRepository.findById(id);
        if (optional.isPresent()) {
            FaceMatchRecord match = optional.get();

            if (!"Waiting".equalsIgnoreCase(newStatus)) {
                String userCode = (String) session.getAttribute("userCode");
                String ip = request.getRemoteAddr();
                match.setStatus(newStatus);
                match.setReviewedBy(userCode);
                match.setReviewedIp(ip);
                match.setReviewedAt(LocalDateTime.now());

                faceMatchRecordRepository.save(match);
            } else {
                System.out.println("🚫 Status 'under_review' is not allowed.");
            }
        } else {
            System.out.println("❌ No match record found for ID: " + id);
        }

        return "redirect:/found/matches";
    }


    // ✅ Mark a match as CLOSED
    @PostMapping("/close/{id}")
    public String closeMatch(@PathVariable Long id, HttpServletRequest request, HttpSession session) {
        FaceMatchRecord record = faceMatchRepo.findById(id).orElse(null);

        if (record != null && !"CLOSED".equalsIgnoreCase(record.getStatus())) {
            record.setStatus("CLOSED");
            record.setReviewedBy((String) session.getAttribute("userCode"));
            record.setReviewedIp(request.getRemoteAddr());
            record.setReviewedAt(LocalDateTime.now());

            faceMatchRepo.save(record);
        }

        return "redirect:/admin/face-match/list";
    }
}
