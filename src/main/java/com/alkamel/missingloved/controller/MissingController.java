package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.MissingRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/missing")
public class MissingController {
    private static final Logger logger = LoggerFactory.getLogger(MissingController.class);

    @Autowired
    private MissingRepository missingRepository;
    private final String imageDir = "C:/kamel/alkwebsite/missingloved/uploads/missing/images";
    private final String docDir = "C:/kamel/alkwebsite/missingloved/uploads/missing/documents";


    // Show create form
    @GetMapping("/create-form")
    public String showCreateForm(Model model) {
        model.addAttribute("missing", new Missing());
        return "missing";
    }


    @PostMapping("/create")
    public String submitForm(@ModelAttribute("missing") Missing missing,
                             @RequestParam("photo") MultipartFile photo,
                             @RequestParam("document") MultipartFile document,
                             HttpServletRequest request,
                             HttpSession session,
                             Model model) {

        try {
            // 🔐 Set createdAt and ipAddress
            missing.setCreatedAt(LocalDateTime.now());
            missing.setCreatedIp(request.getRemoteAddr());

            // 👤 Set createdBy from session if available
            Object userAttr = session.getAttribute("userCode");
            if (userAttr != null) {
                try {
                    missing.setCreatedBy(userAttr.toString());
/*
                    int userCode = Integer.parseInt(userAttr.toString());
                    missing.setCreatedBy(userCode);
  */
                } catch (NumberFormatException ignored) {
                }
            }

            // 🔢 Generate caseCode if empty
            if (missing.getCaseCode() == null || missing.getCaseCode().isBlank()) {
                String caseCode = "MLC-" + System.currentTimeMillis();
                missing.setCaseCode(caseCode);
            }

            // 🖼 Save photo file
            if (photo != null && !photo.isEmpty()) {

                logger.debug("📷 Starting photo upload...");
                String photoFilename = UUID.randomUUID() + "-" + photo.getOriginalFilename();
                File dest = new File("C:\\kamel\\alkwebsite\\missingloved\\uploads\\missing\\images", photoFilename);
                dest.getParentFile().mkdirs();
                photo.transferTo(dest);
                logger.debug("✅ Photo uploaded successfully to " + dest.getAbsolutePath());
                missing.setPhotoFileName(photoFilename);
                missing.setPhotoUrl("uploads/missing/images/" + photoFilename);

            }

            // 📄 Save document file
            if (document != null && !document.isEmpty()) {
                String docFilename = UUID.randomUUID() + "-" + document.getOriginalFilename();
                File dest = new File("C:\\kamel\\alkwebsite\\missingloved\\uploads\\missing\\documents", docFilename);
                dest.getParentFile().mkdirs();
                document.transferTo(dest);
                missing.setOfficialDocPath("uploads/missing/documents/" + docFilename);
            }
            String ip = request.getRemoteAddr();
            missing.setCreatedIp(ip);

            logger.debug("MissingController: setting createdIp to " + request.getRemoteAddr());

            String reporterCode = missing.getReporterCode(); // ✅ Already passed via hidden input

            missing.setCreatedBy(reporterCode); // fallback default

            Object loggedUser = session.getAttribute("loggedUser");
            if (loggedUser instanceof com.alkamel.missingloved.model.User user) {
                missing.setCreatedBy(user.getUserCode()); // ✅ Use logged in user
            }

            // 💾 Save record
            missingRepository.save(missing);

            model.addAttribute("successMessage", "✅ تم إرسال البلاغ بنجاح");
            return "reportmlcmisp";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "حدث خطأ أثناء رفع الملفات");
            return "reportmlcmisp";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "حدث خطأ غير متوقع");
            return "reportmlcmisp";
        }
    }

    // View missing details
    @GetMapping("/view/{id}")
    public String viewMissingDetails(@PathVariable Integer id, Model model, HttpSession session) {
        Missing missing = missingRepository.findById(id).orElse(null);
        if (missing == null) {
            model.addAttribute("errorMessage", "لم يتم العثور على هذه الحالة");
            return "error-page";
        }
        model.addAttribute("missing", missing);

        // ✅ Save caseCode to session
        session.setAttribute("sightmis_missingcode", missing.getCaseCode());

        return "missing-detail-view";
    }
}

/*
   @GetMapping("/missing/view/{id}")
   public String viewMissingDetails(@PathVariable Integer id, Model model) {
        Missing missing = missingRepository.findById(id).orElse(null);
        if (missing == null) {
            return "redirect:/";
        }
        model.addAttribute("missing", missing);
        return "missing-detail-view";
    }
}
*/