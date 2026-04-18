
package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Sightmis;
import com.alkamel.missingloved.repository.MissingRepository;
import com.alkamel.missingloved.repository.SightmisRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpSession;

@Controller
public class SightmisController {

    @Autowired
    private SightmisRepository sightmisRepository;

    @Autowired
    private MissingRepository missingRepository;

    @PostMapping("/sightmis/delete/{id}")
    public String deleteSightmis(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (sightmisRepository.existsById(id)) {
            sightmisRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "تم حذف البلاغ بنجاح");
        } else {
            redirectAttributes.addFlashAttribute("message", "⚠️ لم يتم العثور على البلاغ المطلوب");
        }
        return "redirect:/sightmis/list";
    }

    @GetMapping("/sightmis")
    public String showSightmisForm(@RequestParam(value = "userCode", required = false) String userCode,
                                   @RequestParam(value = "missingcode", required = false) String missingcodeParam,
                                   HttpSession session,
                                   Model model) {

        // 1️⃣ جلب missingcode
        String resolvedMissingCode = (missingcodeParam != null)
                ? missingcodeParam
                : (String) session.getAttribute("sightmis_missingcode");

        // 2️⃣ إنشاء النموذج
        Sightmis sightmis = new Sightmis();

        if (resolvedMissingCode != null) {
            sightmis.setMissingcode(resolvedMissingCode);
            System.out.println("✅ [DEBUG] loaded missingcode = " + resolvedMissingCode);
        } else {
            System.out.println("⚠️ [DEBUG] missingcode not found in URL or session");
        }

        if (userCode != null) {
            sightmis.setMseenreportedby(userCode);
            System.out.println("✅ [DEBUG] loaded userCode = " + userCode);
        }
        sightmis.setMseencountry("مصر");
        model.addAttribute("sightmis", sightmis);
        return "sightmis";
    }

/*    @GetMapping("/sightmis")
    public String showSightmisForm(@RequestParam(value = "userCode", required = false) String userCode,
                                   @RequestParam(value = "missingcode", required = false) String missingcodeParam,
                                   HttpSession session,
                                   Model model) {

        // If not passed in the URL, try to load from session
        if (missingcodeParam == null) {
            missingcodeParam = (String) session.getAttribute("sightmis_missingcode");
        }

        // Populate default sightmis object
        Sightmis sightmis = new Sightmis();
        sightmis.setMissingcode(missingcodeParam); // ✅ pre-fill
        sightmis.setMseenreportedby(userCode);     // ✅ pre-fill reporter if needed

        model.addAttribute("sightmis", sightmis);
        return "sightmis";
    }
*/
/*    @GetMapping("/sightmis")
    public String showSightmisForm(
            @RequestParam(value = "userCode", required = false) String userCode,
            @RequestParam(value = "missingcode", required = false) String missingcode,
            Model model) {

        System.out.println("🧭 [DEBUG] GET /sightmis -> userCode = " + userCode);
        System.out.println("🧭 [DEBUG] GET /sightmis -> missingcode = " + missingcode);

        model.addAttribute("sightmis", new Sightmis());
        return "sightmis";
    }
*/

    @GetMapping("/sightmis/check-casecode/{code}")
    @ResponseBody
    public boolean checkCaseCode(@PathVariable("code") String code) {
        Long existsResult = sightmisRepository.checkCaseCodeExists(code);
        return (existsResult != null && existsResult == 1);
    }

    @GetMapping("/sightmis/list")
    public String listSightmis(Model model, HttpServletRequest request) {
        List<Sightmis> sightmisList = sightmisRepository.findAll();
        model.addAttribute("sightmisList", sightmisList);

        // Pass CSRF token to the template to avoid Thymeleaf errors
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "sightmis-list";
    }

    @PostMapping("/sightmis/activate/{id}")
    public String activateSightmis(@PathVariable Long id, HttpServletRequest request, Principal principal) {
        System.out.println("🔍 [DEBUG] principal = " + principal);
        System.out.println("🔍 [DEBUG] principal.getName() = " + (principal != null ? principal.getName() : "null"));

        Sightmis s = sightmisRepository.findById(id).orElse(null);
        if (s != null && s.getMseenactivatedby() == null) {
            s.setMseenactivatedby(principal != null ? principal.getName() : "anonymous");
            s.setMseenactivatedip(getClientIp(request));
            s.setMseenactivatedat(LocalDateTime.now());
            s.setMseenactivationcode("act-" + UUID.randomUUID().toString().substring(0, 8));
            sightmisRepository.save(s);
        }
        return "redirect:/sightmis/list";
    }

    @PostMapping("/sightmis/deactivate/{id}")
    public String deactivateSightmis(@PathVariable Long id, HttpServletRequest request, Principal principal) {
        System.out.println("🔍 [DEBUG] principal = " + principal);
        System.out.println("🔍 [DEBUG] principal.getName() = " + (principal != null ? principal.getName() : "null"));

        Optional<Sightmis> sightmisOpt = sightmisRepository.findById(id);
        if (sightmisOpt.isPresent()) {
            Sightmis sightmis = sightmisOpt.get();
            sightmis.setMseendeactivatedat(LocalDateTime.now());
            sightmis.setMseendeactivatedby(principal != null ? principal.getName() : "anonymous");
            sightmis.setMseendeactivatedip(getClientIp(request));
            sightmis.setMseenactivatedby(null);
            sightmis.setMseenactivatedip(null);
            sightmis.setMseenactivatedat(null);
            sightmisRepository.save(sightmis);
        }
        return "redirect:/sightmis/list";
    }

    @PostMapping("/sightmis/create")
    public String createSightmis(@ModelAttribute("sightmis") Sightmis sightmis,
                                 BindingResult result,
                                 Model model,
                                 HttpServletRequest request) {
        if (sightmis.getMseencreatedby() == null || sightmis.getMseencreatedby().isBlank()) {
            String creator = request.getParameter("mseencreatedby");
            if (creator != null) sightmis.setMseencreatedby(creator);
        }
        if (sightmis.getMseenreportedby() == null || sightmis.getMseenreportedby().isBlank()) {
            String reporter = request.getParameter("mseenreportedby");
            if (reporter != null) sightmis.setMseenreportedby(reporter);
        }

        sightmis.setMseencreatedip(getClientIp(request));
        sightmis.setMseencreatedat(LocalDateTime.now());

        if (sightmis.getSightmiscode() == null || sightmis.getSightmiscode().isEmpty()) {
            String randomCode = "sig-" + UUID.randomUUID().toString().substring(0, 8);
            sightmis.setSightmiscode(randomCode);
        }

        Long existsResult = sightmisRepository.checkCaseCodeExists(sightmis.getMissingcode());
        if (existsResult == null || existsResult != 1) {
            result.rejectValue("missingcode", "error.sightmis", "⚠️ الكود غير موجود في قاعدة بيانات الغائبين");
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "يرجى تصحيح الأخطاء في النموذج");
            return "sightmis";
        }

        sightmis.setMseencreatedip(getClientIp(request));
        sightmis.setMseencreatedat(LocalDateTime.now());
        sightmis.setReportdate(LocalDate.now());
        sightmis.setMseenactivatedby(null);
        sightmis.setMseenactivatedip(null);
        sightmis.setMseenactivatedat(null);

        sightmisRepository.save(sightmis);
        model.addAttribute("successMessage", "✅ تم إرسال بلاغ المشاهدة بنجاح");
        return "sightmis";
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader != null) ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}
