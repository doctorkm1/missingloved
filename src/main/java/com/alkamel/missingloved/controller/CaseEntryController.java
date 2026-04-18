package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.MissingRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.alkamel.missingloved.repository.UserRepository;
import com.alkamel.missingloved.model.User;


import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;


import java.util.Map;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import com.alkamel.missingloved.service.MissingService;

@Controller
@RequestMapping("/case")
public class CaseEntryController {

    @Autowired
    private MissingService missingService;
    @Autowired
    private UserRepository userRepository;


    private final MissingRepository missingRepository;
    private final JavaMailSender mailSender;

    @Value("${file.upload.missing.image-dir}")
    private String imageDir;

    @Value("${file.upload.missing.doc-dir}")
    private String docDir;

    @Value("${admin.email}")
    private String adminEmail;

    public CaseEntryController(MissingRepository missingRepository, JavaMailSender mailSender) {
        this.missingRepository = missingRepository;
        this.mailSender = mailSender;
    }

    @GetMapping("/case-entry")
    public String showForm(Model model) {
        model.addAttribute("missing", new Missing());
        System.out.println("✅ Returning to case-entry");
        return "case-entry";
    }



    @PostMapping("/case-entry")
    public String handleForm(
            @Valid @ModelAttribute("missing") Missing missing,
            BindingResult result,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("document") MultipartFile document,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "documentNumber", required = false) String documentNumber,
            @RequestParam(value = "documentIssuedBy", required = false) String documentIssuedBy,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(value = "documentDate", required = false) Date documentDate,
            HttpServletRequest request,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "يرجى تصحيح الأخطاء في الحقول المطلوبة.");
            System.out.println("✅ Returning to case-entry");
            return "case-entry";
        }

        try {
            new File(imageDir).mkdirs();
            new File(docDir).mkdirs();

            String photoPath = null;
            if (!photo.isEmpty()) {
                String photoFilename = UUID.randomUUID() + "-" + photo.getOriginalFilename();
                File photoFile = new File(imageDir, photoFilename);
                photo.transferTo(photoFile);
                photoPath = "uploads/missing/images/" + photoFilename;
            }

            String docPath = null;
            if (!document.isEmpty()) {
                String docFilename = UUID.randomUUID() + "-" + document.getOriginalFilename();
                File docFile = new File(docDir, docFilename);
                document.transferTo(docFile);
                docPath = "uploads/missing/documents/" + docFilename;
            }

            String caseCode = "MLC-" + System.currentTimeMillis();
            String ip = request.getRemoteAddr();

            missing.setCaseCode(caseCode);
            missing.setCreatedIp(ip);
            missing.setCreatedAt(LocalDateTime.now());
            missing.setActivated(false);
            missing.setPhotoUrl(photoPath);
            missing.setOfficialDocPath(docPath);
            missing.setDocumentType(documentType);
            missing.setDocumentNumber(documentNumber);
            missing.setDocumentIssuedBy(documentIssuedBy);

            if (documentDate != null) {
                LocalDate localDocumentDate = documentDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                missing.setDocumentDate(localDocumentDate);
            }

            missingRepository.save(missing);
            model.addAttribute("successMessage", "تم تسجيل البلاغ بنجاح. سيتم الإشهار في أقرب وقت.");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "فشل في تسجيل البلاغ: " + e.getMessage());
        }

        System.out.println("✅ Returning to case-entry");
        return "case-entry";
    }


    @GetMapping("/list")
    public String filterCases(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        List<Missing> all = missingRepository.findAll().stream()
                .filter(m -> city == null || city.isBlank() || m.getCity().equalsIgnoreCase(city))
                .filter(m -> {
                    if ("active".equalsIgnoreCase(type)) return m.isActivated();
                    if ("inactive".equalsIgnoreCase(type)) return !m.isActivated();
                    return true;
                })
                .filter(m -> from == null || (m.getCreatedAt() != null && !m.getCreatedAt().toLocalDate().isBefore(from)))
                .filter(m -> to == null || (m.getCreatedAt() != null && !m.getCreatedAt().toLocalDate().isAfter(to)))
                .toList();

        int start = Math.min(page * size, all.size());
        int end = Math.min((page + 1) * size, all.size());

        List<Missing> pageList = all.subList(start, end);

        model.addAttribute("missingList", pageList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) all.size() / size));

        model.addAttribute("city", city);
        model.addAttribute("type", type);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "case-list";
    }


    @GetMapping("/missing/next")
    @ResponseBody
    public ResponseEntity<Missing> getNextMissing(@RequestParam int index) {
        List<Missing> active = missingRepository.findByActivatedTrueOrderByCreatedAtDesc();
        if (index < 0 || index >= active.size()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(active.get(index));
    }

    @GetMapping("/missing/count")
    @ResponseBody
    public int getActiveMissingCount() {
        return missingRepository.countByActivatedTrue();
    }


    @GetMapping("/missing/next-filtered")
    @ResponseBody
    public Missing getFilteredNext(
            @RequestParam int index,
            @RequestParam(required = false) String governorate,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (governorate != null && governorate.trim().isEmpty()) governorate = null;
        if (city != null && city.trim().isEmpty()) city = null;

        List<Missing> filtered = missingRepository.findFiltered(governorate, city, fromDate, toDate);
        if (index >= 0 && index < filtered.size()) {
            return filtered.get(index);
        } else {
            return null;
        }
    }

    @GetMapping("/missing/count-filtered")
    @ResponseBody
    public int countFiltered(
            @RequestParam(required = false) String governorate,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (governorate != null && governorate.trim().isEmpty()) governorate = null;
        if (city != null && city.trim().isEmpty()) city = null;

        return missingRepository.countFiltered(governorate, city, fromDate, toDate);
    }

    @PostMapping("/activate/{id}")
    public String activateCase(@PathVariable Integer id,
                               @RequestParam Map<String, String> params,
                               HttpSession session,
                               HttpServletRequest request) {

        // Load logged-in user (by ID or session object)
        User loggedUser = null;
        Object userAttr = session.getAttribute("loggedInUserId");
        if (userAttr instanceof Number) {
            loggedUser = userRepository.findById(((Number) userAttr).longValue()).orElse(null);
        } else if (session.getAttribute("loggedUser") instanceof User) {
            loggedUser = (User) session.getAttribute("loggedUser");
        }

        // Activate case
        Missing missing = missingRepository.findById(id).orElseThrow();
        missing.setActivated(true);
        missing.setActivatedAt(LocalDateTime.now());

        if (loggedUser != null) {
            missing.setActivatedBy(loggedUser);  // Store as foreign key (User)
        }

        missing.setActivatedIp(request.getRemoteAddr());
        missingRepository.save(missing);

        return redirectWithParams(params);
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateCase(@PathVariable Integer id,
                                 @RequestParam Map<String, String> params,
                                 HttpSession session,
                                 HttpServletRequest request) {

        // Load logged-in user (by ID or session object)
        User loggedUser = null;
        Object userAttr = session.getAttribute("loggedInUserId");
        if (userAttr instanceof Number) {
            loggedUser = userRepository.findById(((Number) userAttr).longValue()).orElse(null);
        } else if (session.getAttribute("loggedUser") instanceof User) {
            loggedUser = (User) session.getAttribute("loggedUser");
        }

        // Deactivate case
        Missing missing = missingRepository.findById(id).orElseThrow();
        missing.setActivated(false);
        missing.setDeactivatedAt(LocalDateTime.now());

        if (loggedUser != null) {
            missing.setDeactivatedBy(loggedUser);  // Store as foreign key (User)
        }

        missing.setDeactivatedIp(request.getRemoteAddr());
        missingRepository.save(missing);

        return redirectWithParams(params);
    }

    @PostMapping("/delete/{id}")
    public String deleteCase(@PathVariable Integer id,
                             @RequestParam Map<String, String> params) {
        missingService.delete(id);
        return redirectWithParams(params);
    }

    private String redirectWithParams(Map<String, String> params) {
        String query = params.entrySet().stream()
                .filter(e -> e.getKey().matches("page|city|type|from|to"))
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return "redirect:/case/list" + (query.isEmpty() ? "" : "?" + query);
    }

}