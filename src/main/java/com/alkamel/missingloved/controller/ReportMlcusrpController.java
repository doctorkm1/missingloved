package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.repository.ReportMlcusrpRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.alkamel.missingloved.service.UserService;

@Controller
public class ReportMlcusrpController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportMlcusrpRepository reportMlcusrpRepository;

    @GetMapping("/reportmlcusrp")
    public String showReportForm(@RequestParam(value = "next", required = false) String next,
                                 Model model,
                                 HttpSession session,
                                 HttpServletRequest request) {

        User user = new User();
        user.setCountry("مصر");
        model.addAttribute("user", user);
        model.addAttribute("forceErrors", false);

        // ✅ Allow all clean next paths, not checking template
        boolean isValidNext = next != null && next.matches("^[a-zA-Z0-9/_-]+$");

        if (isValidNext) {
            session.setAttribute("nextStep", next);
            model.addAttribute("nextIsInvalid", false);
            model.addAttribute("next", next);
        } else {
            String referer = request.getHeader("Referer");
            String backUrl = (referer != null) ? referer : "/";
            String errorHtml = "<span style='font-size:1.2em;'>"
                    + "خطأ: لم يتم تحديد وجهة البلاغ التالية بشكل صحيح. إضغط إستمرار للخروج "
                    + "<a href='" + backUrl + "' class='btn btn-danger btn-sm ms-3'>استمرار</a>"
                    + "</span>";

            model.addAttribute("errorMessage", errorHtml);
        }

        return "reportmlcusrp";
    }

    @GetMapping("/custom-reportmlcusrp")
    public String customReportUserWithMissingCode(@RequestParam("missingcode") String missingcode,
                                                  @RequestParam(name = "next", defaultValue = "sightmis") String next,
                                                  HttpSession session,
                                                  Model model) {
        session.setAttribute("sightmis_missingcode", missingcode);
        model.addAttribute("user", new User());

        // ✅ Needed so Thymeleaf can populate the hidden "next" input
        model.addAttribute("next", next);

        return "reportmlcusrp";
    }

    @PostMapping("/custom-reportmlcusrp")
    public String saveUserWithRedirect(@ModelAttribute User user,
                                       @RequestParam("next") String next,
                                       HttpSession session,
                                       Model model) {
        try {
            // Save the user
            User savedUser = userService.createUser(user);

            // Store usercode for the next step
            session.setAttribute("usercode", savedUser.getUserCode());

            // Get missingcode from session (set earlier in GET method)
            String missingcode = (String) session.getAttribute("sightmis_missingcode");

            // Redirect based on next
            if ("sightmis".equals(next)) {
                return "redirect:/sightmis?userCode=" + savedUser.getUserCode() + "&missingcode=" + missingcode;
            } else {
                return "redirect:/"; // fallback
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠️ حدث خطأ أثناء حفظ بيانات المبلغ: " + e.getMessage());
            return "reportmlcusrp";
        }
    }

    @PostMapping("/reportmlcusrp")
    public String handleReportForm(@ModelAttribute("user") @Valid User user,
                                   BindingResult result,
                                   Model model,
                                   HttpServletRequest request) {

        // ✅ Retain next step
        String nextParam = request.getParameter("next");
        if (nextParam != null && !nextParam.isEmpty()) {
            request.getSession().setAttribute("nextStep", nextParam);
        }

        if (reportMlcusrpRepository.existsByNationalId(user.getNationalId())) {
            model.addAttribute("errorMessage", "هذا الرقم القومي مستخدم بالفعل.");
            model.addAttribute("user", user);
            model.addAttribute("nextStep", request.getSession().getAttribute("nextStep"));
            return "reportmlcusrp";
        }

        if (reportMlcusrpRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "هذا البريد الإلكتروني مستخدم بالفعل.");
            model.addAttribute("user", user);
            String next = (String) request.getSession().getAttribute("nextStep");
            if (next != null && !next.trim().isEmpty()) {
                return "redirect:/" + next;
            }
            return "reportmlcusrp";
        }

        if (result.hasErrors()) {
            model.addAttribute("forceErrors", true);
            return "reportmlcusrp";
        }

        // === Inject metadata ===
        String ip = request.getRemoteAddr();
        user.setcreatedIp(ip);
        user.setCreatedAt(java.time.LocalDateTime.now());

        Object sessionUser = request.getSession().getAttribute("loggedUser");
        if (sessionUser instanceof User loggedUser) {
            user.setCreatedBy(loggedUser.getUserCode());
        } else {
            user.setCreatedBy(user.getUserCode());
        }

        reportMlcusrpRepository.save(user);
        model.addAttribute("successMessage", "✅ تم التسجيل بنجاح");
        model.addAttribute("userCode", user.getUserCode());
        model.addAttribute("user", new User());
        model.addAttribute("nextStep", request.getSession().getAttribute("nextStep"));

        return "reportmlcusrp";
    }

    // ✅ This method checks if the .html template exists
    private boolean templateExists(String viewName) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + viewName + ".html");
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
