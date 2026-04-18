package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.MissingRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reportmlcmisp")
public class ReportMlcmispController {

    @Autowired
    private MissingRepository missingRepository;

    @GetMapping
    public String showForm(@RequestParam(name = "userCode", required = false) String userCode,
                           HttpSession session,
                           Model model) {

        Missing missing = new Missing();
        missing.setCountry("مصر");          // default like FoundController
        missing.setLastSeenCountry("مصر");  // also set for the last seen section if

        // Prioritize logged-in user session
        String sessionUserCode = (String) session.getAttribute("userCode");
        if (sessionUserCode != null && !sessionUserCode.trim().isEmpty()) {
            missing.setCreatedBy(sessionUserCode);
        } else if (userCode != null && !userCode.trim().isEmpty()) {
            missing.setCreatedBy(userCode);
        }

        model.addAttribute("missing", missing);
        return "reportmlcmisp";
    }

    @PostMapping
    public String handleForm(@ModelAttribute("missing") Missing missing,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "حدث خطأ أثناء التحقق من البيانات.");
            return "reportmlcmisp";
        }

        missing.setCreatedAt(LocalDateTime.now());
        missingRepository.save(missing);

        model.addAttribute("successMessage", "تم تسجيل بلاغ الغائب بنجاح.");
        return "reportmlcmisp";
    }
}
