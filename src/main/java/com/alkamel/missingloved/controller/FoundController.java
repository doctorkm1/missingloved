package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.FaceMatchRecord;
import com.alkamel.missingloved.model.Found;
import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.FaceMatchRecordRepository;
import com.alkamel.missingloved.repository.FoundRepository;
import com.alkamel.missingloved.repository.MissingRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.alkamel.missingloved.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.alkamel.missingloved.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

@Slf4j
@Controller
@RequestMapping("/found")
public class FoundController {


    private final FoundRepository foundRepository;
    private final FaceMatchRecordRepository faceMatchRepo;
    private final MissingRepository missingRepository;
    @Autowired
    private UserRepository userRepository;



    public FoundController(FoundRepository foundRepository, FaceMatchRecordRepository faceMatchRepo, MissingRepository missingRepository) {
        this.foundRepository = foundRepository;
        this.faceMatchRepo = faceMatchRepo;
        this.missingRepository = missingRepository;
    }

    @Value("${file.upload.found.image-dir}")
    private String imageDir;

    @Value("${file.upload.found.doc-dir}")
    private String docDir;

    @PostConstruct
    public void checkDocDirPath() {
        System.out.println("### Loaded docDir path: " + docDir);
    }

    @GetMapping("/found/row/{id}")
    public String getFoundRow(@PathVariable Integer id, Model model) {
        Found found = foundRepository.findById(id).orElse(null);
        model.addAttribute("f", found);
        return "fragments/found-row :: row";
    }


    @GetMapping("/report")
    public String showFoundForm(@RequestParam(required = false) String userCode,
                                HttpSession session,
                                Model model) {
        if (userCode != null) {
            session.setAttribute("generatedUserCode", userCode);  // ✅ capture into session
        }

        Found found = new Found();
        found.setCountry("مصر");
        model.addAttribute("found", found);
        return "found-entry";
    }

    @PostMapping("/report")
    public String submitFoundForm(
            @Valid @ModelAttribute("found") Found found,
            BindingResult result,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("document") MultipartFile document,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "documentNumber", required = false) String documentNumber,
            @RequestParam(value = "documentIssuedBy", required = false) String documentIssuedBy,
            @RequestParam(value = "documentDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentDate,
            HttpServletRequest request,
            Model model
    )

    {
        // Debug: Print field states before any validation or save
        log.debug("Inspecting Found record fields before validation:");
        log.debug("Governorate: {}", found.getGovernorate());
        log.debug("City: {}", found.getCity());
        log.debug("District (optional): {}", found.getDistrict());
        log.debug("DocumentType: {}", documentType);
        log.debug("DocumentNumber: {}", documentNumber);
        log.debug("DocumentIssuedBy: {}", documentIssuedBy);
        log.debug("DocumentDate: {}", documentDate);
        log.debug("Photo empty? {}", photo.isEmpty());
        log.debug("Document empty? {}", document.isEmpty());

        if (result.hasErrors()
                || documentType == null || documentType.isBlank()
                || documentNumber == null || documentNumber.isBlank()
                || documentIssuedBy == null || documentIssuedBy.isBlank()
                || documentDate == null
                || photo.isEmpty()
                || document.isEmpty()) {

            // Debug BEFORE returning the error message
            log.debug("Validation failed - Checking which fields are missing:");
            if (found.getGovernorate() == null) log.debug("Governorate is NULL");
            if (found.getCity() == null) log.debug("City is NULL");
            if (found.getDistrict() == null) log.debug("District is NULL (optional)");
            if (documentType == null || documentType.isBlank()) log.debug("DocumentType is NULL or blank");
            if (documentNumber == null || documentNumber.isBlank()) log.debug("DocumentNumber is NULL or blank");
            if (documentIssuedBy == null || documentIssuedBy.isBlank()) log.debug("DocumentIssuedBy is NULL or blank");
            if (documentDate == null) log.debug("DocumentDate is NULL");
            if (photo.isEmpty()) log.debug("Photo file is empty");
            if (document.isEmpty()) log.debug("Document file is empty");

            model.addAttribute("errorMessage", "يرجى استكمال جميع الحقول الإلزامية وإرفاق الصور.");
            return "found-entry";
        }

        try {
            new File(imageDir).mkdirs();
            new File(docDir).mkdirs();

            String uploadedPhotoFileName = UUID.randomUUID() + "-" + photo.getOriginalFilename();
            File photoFile = new File(imageDir, uploadedPhotoFileName);
            photo.transferTo(photoFile);
            String photoPath = "uploads/found/images/" + uploadedPhotoFileName;
            found.setPhotoUrl(photoPath);
            found.setPhotoFileName(uploadedPhotoFileName);

            String docName = UUID.randomUUID() + "-" + document.getOriginalFilename();
            File docFile = new File(docDir, docName);
            document.transferTo(docFile);
            String docPath = "uploads/found/documents/" + docName;
            found.setOfficialDocPath(docPath);

            found.setCreatedAt(LocalDateTime.now());
            found.setActivated(false);
            found.setCaseCode("FND-" + System.currentTimeMillis());
            found.setCreatedIp(request.getRemoteAddr());
            found.setDocumentType(documentType);
            found.setDocumentNumber(documentNumber);
            found.setDocumentIssuedBy(documentIssuedBy);
            found.setDocumentDate(documentDate);
            String ip = request.getRemoteAddr();

            User createdUser = null;
            HttpSession session = request.getSession();

            Object sessionUserCode = session.getAttribute("generatedUserCode");
            if (sessionUserCode != null) {

                Optional<User> optionalUser = userRepository.findByUserCode(sessionUserCode.toString());
                if (optionalUser.isPresent()) {
                    createdUser = optionalUser.get();
                }

            } else if (session.getAttribute("loggedUser") != null) {
                User loggedUser = (User) session.getAttribute("loggedUser");
                Optional<User> optionalUser = userRepository.findByUserCode(loggedUser.getUserCode());
                if (optionalUser.isPresent()) {
                    createdUser = optionalUser.get();
                }
            }


// ✅ Unified createdBy and reporterCode assignment
            Object generatedUserCode = session.getAttribute("generatedUserCode");
            Object loggedUserObj = session.getAttribute("loggedUser"); // ✅ Corrected key

// ✅ Always set ReporterCode from generated code if exists
            if (generatedUserCode != null) {
                String code = generatedUserCode.toString();
                found.setReporterCode(code);
            }

// ✅ createdBy priority: logged-in user > generated user
            if (loggedUserObj instanceof User loggedUser) {
                userRepository.findByUserCode(loggedUser.getUserCode()).ifPresent(found::setCreatedBy);
            } else if (generatedUserCode != null) {
                String code = generatedUserCode.toString();
                userRepository.findByUserCode(code).ifPresent(found::setCreatedBy);
            }


            /*            found.setCreatedBy(createdUser);  */
/*            found.setCreatedAt(LocalDateTime.now());
            found.setCreatedIp(ip);
            found.setActivatedAt(LocalDateTime.now());
            found.setActivatedIp(ip);  */

// Debug: Print any null fields before saving
            log.debug("Saving Found record - Checking for null fields:");
            if (found.getGovernorate() == null) log.debug("Government is NULL");
            if (found.getCity() == null) log.debug("City is NULL");
            if (found.getDistrict() == null) log.debug("District is NULL (optional, so may be okay)");
            // --- Handle new host location and person fields explicitly ---
            if (found.gethCountry() == null || found.gethCountry().isEmpty()) {
                found.sethCountry("مصر"); // default if not provided
            }
            found.sethGovernorate(found.gethGovernorate());
            found.sethCity(found.gethCity());
            found.sethDistrict(found.gethDistrict());
            found.sethAddress(found.gethAddress());
            found.sethHostName(found.gethHostName());
            found.sethMobile(found.gethMobile());
            found.sethLandline(found.gethLandline());
            foundRepository.save(found);

            if (!photo.isEmpty()) {
                String fullPhotoPath = photoFile.getAbsolutePath();
                System.out.println("🔍 Running face recognition on: " + fullPhotoPath);
                String matchOutput = runFaceRecognitionScript(fullPhotoPath);
                boolean matchSaved = false;

                if (matchOutput != null && matchOutput.contains("MATCH FOUND:")) {
                    String[] lines = matchOutput.split("\n");

                    for (String line : lines) {
                        System.out.println("📄 PYTHON OUTPUT LINE: " + line);
                        if (!line.startsWith("MATCH FOUND:")) continue;

                        try {
                            String[] parts = line.replace("MATCH FOUND:", "").trim().split(":");
                            if (parts.length < 2) {
                                System.out.println("⚠️ Malformed match line: " + line);
                                continue;
                            }

                            String matchedFileName = parts[0].trim();
                            double similarity = Double.parseDouble(parts[1].trim());

                            System.out.println("➡️ Looking for missing record with file: " + matchedFileName);
                            Missing matchedMissing = missingRepository.findByPhotoFileName(matchedFileName);

                            if (matchedMissing == null) {
                                System.out.println("❌ No match found in DB for filename: " + matchedFileName);
                                continue;
                            }

                            FaceMatchRecord record = new FaceMatchRecord();
                            record.setMissing(matchedMissing);
                            record.setFound(found);
                            record.setSimilarity(similarity);
                            record.setStatus("UNDER_REVIEW");
                            record.setMatchDate(LocalDateTime.now());

                            faceMatchRepo.save(record);
                            matchSaved = true;
                            System.out.println("✅ Match record saved: foundId=" + found.getId() + ", missingId=" + matchedMissing.getId());

                        } catch (Exception e) {
                            System.out.println("❌ Exception during match parsing/saving: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    if (matchSaved) {
                        model.addAttribute("warningMessage", "⚠️ تم العثور على تطابق محتمل");
                    }
                } else {
                    System.out.println("❌ No 'MATCH FOUND:' in output or matchOutput is null.");
                }
            }

            model.addAttribute("successMessage", "✅ تم تسجيل البلاغ بنجاح.");

        } catch (IOException e) {
            model.addAttribute("errorMessage", "حدث خطأ أثناء رفع الملفات: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "فشل في حفظ البلاغ: " + e.getMessage());
            e.printStackTrace();
        }

        return "found-entry";
    }

    @PostMapping("/activate/{id}")
    public String activateFound(@PathVariable Integer id,
                                @RequestParam(required = false) String anchor,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {  // ✅ Fix: add request

        Found found = foundRepository.findById(id).orElse(null);
        if (found == null) {
            System.out.println("❌ [DEBUG] No record found for ID: " + id);
            redirectAttributes.addFlashAttribute("error", "البلاغ غير موجود.");
            return "redirect:/found/list" + (anchor != null ? "#row-" + anchor : "");
            /*       return "redirect:/found/list";  */
        }
        System.out.println("📌 [DEBUG] Retrieved Found record: " + (found != null ? found.toString() : "null"));

        found.setActivated(true);
        found.setActivatedAt(LocalDateTime.now());

        String ip = request.getRemoteAddr();
        HttpSession session = request.getSession();
        User createdUser = null;

        Object sessionUserCode = session.getAttribute("generatedUserCode");
        if (sessionUserCode != null) {
            Optional<User> optionalUser = userRepository.findByUserCode(sessionUserCode.toString());  // ✅ Fix
            if (optionalUser.isPresent()) {
                createdUser = optionalUser.get();
            }
        } else if (session.getAttribute("loggedUser") != null) { // ✅ Correct key
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser != null) {
                Optional<User> optionalUser = userRepository.findByUserCode(loggedUser.getUserCode());
                if (optionalUser.isPresent()) {
                    createdUser = optionalUser.get();
                }
            }
        }

        found.setActivated(true);
        found.setActivatedAt(LocalDateTime.now());
        found.setActivatedIp(ip);

// ✅ Assign activatedBy (priority: loggedUser > generatedUser)
        if (session.getAttribute("loggedUser") instanceof User loggedUser) {
            userRepository.findByUserCode(loggedUser.getUserCode()).ifPresent(found::setActivatedBy);
        } else if (sessionUserCode != null) {
            userRepository.findByUserCode(sessionUserCode.toString()).ifPresent(found::setActivatedBy);
        }

        // --- Handle new host location and person fields explicitly ---
        if (found.gethCountry() == null || found.gethCountry().isEmpty()) {
            found.sethCountry("مصر"); // default if not provided
        }
        found.sethGovernorate(found.gethGovernorate());
        found.sethCity(found.gethCity());
        found.sethDistrict(found.gethDistrict());
        found.sethAddress(found.gethAddress());
        found.sethHostName(found.gethHostName());
        found.sethMobile(found.gethMobile());
        found.sethLandline(found.gethLandline());
        foundRepository.save(found);
        return "redirect:/found/list" + (anchor != null ? "#row-" + anchor : "");
        /*        return "redirect:/found/list"; */
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateFound(@PathVariable Integer id,
                                  @RequestParam(required = false) String anchor,
                                  HttpServletRequest request) {
        System.out.println("📌 Deactivating found case ID: " + id); // DEBUG
        Found found = foundRepository.findById(id).orElseThrow();
        found.setActivated(false);
        found.setDeactivatedAt(LocalDateTime.now());
        found.setDeactivatedIp(request.getRemoteAddr());

// ✅ Assign deactivatedBy (priority: loggedUser > generatedUser)
        HttpSession session = request.getSession();
        Object sessionUserCode = session.getAttribute("generatedUserCode");

        if (session.getAttribute("loggedUser") instanceof User loggedUser) {
            userRepository.findByUserCode(loggedUser.getUserCode()).ifPresent(found::setDeactivatedBy);
        } else if (sessionUserCode != null) {
            userRepository.findByUserCode(sessionUserCode.toString()).ifPresent(found::setDeactivatedBy);
        }
        // --- Handle new host location and person fields explicitly ---
        if (found.gethCountry() == null || found.gethCountry().isEmpty()) {
            found.sethCountry("مصر"); // default if not provided
        }
        found.sethGovernorate(found.gethGovernorate());
        found.sethCity(found.gethCity());
        found.sethDistrict(found.gethDistrict());
        found.sethAddress(found.gethAddress());
        found.sethHostName(found.gethHostName());
        found.sethMobile(found.gethMobile());
        found.sethLandline(found.gethLandline());
        foundRepository.save(found);
        return "redirect:/found/list" + (anchor != null ? "#row-" + anchor : "");
 /*       return "redirect:/found/list#found-" + id;   -->
/*        return "redirect:" + request.getHeader("Referer") + "#row-" + id; */
        /*       return "redirect:/found/list";  */
    }

    @GetMapping("/view/{id}")
    public String viewFoundDetails(@PathVariable Integer id, Model model) {
        Found found = foundRepository.findById(id).orElse(null);
        if (found == null) {
            model.addAttribute("errorMessage", "لم يتم العثور على هذه الحالة");
            return "error-page";
        }
        model.addAttribute("found", found);
        return "found-view";
    }

    @Transactional
    @PostMapping("/found/delete/{id}")
    public String deleteFound(@PathVariable Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Optional<Found> optional = foundRepository.findById(id);
        System.out.println("📌 Delete postmapping found Delete ID: " + id); // DEBUG
        if (optional.isPresent()) {
            System.out.println("📌 EXIST postmapping found Delete ID: " + id); // DEBUG
            foundRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "تم حذف البلاغ بنجاح.");
            System.out.println("📌 success postmapping found Delete ID: " + id); // DEBUG
        } else {
            System.out.println("📌 failed postmapping found Delete ID: " + id); // DEBUG
            redirectAttributes.addFlashAttribute("errorMessage", "لم يتم العثور على البلاغ.");
        }
        System.out.println("📌 End of postmapping found Delete ID: " + id); // DEBUG
        return "redirect:" + request.getHeader("Referer") + "#row-" + id;
        /*       return "redirect:/found/list";  */
    }


    @GetMapping("/list")
    public String listFoundCases(@RequestParam(required = false) String city,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {

        List<Found> all = foundRepository.findAll().stream()
                .filter(f -> city == null || city.isBlank() || f.getCity().equalsIgnoreCase(city))
                .filter(f -> {
                    if ("active".equalsIgnoreCase(type)) return f.isActivated();
                    if ("inactive".equalsIgnoreCase(type)) return !f.isActivated();
                    return true;
                })
                .filter(f -> from == null || (f.getCreatedAt() != null && !f.getCreatedAt().toLocalDate().isBefore(from)))
                .filter(f -> to == null || (f.getCreatedAt() != null && !f.getCreatedAt().toLocalDate().isAfter(to)))
                .toList();

        int start = Math.min(page * size, all.size());
        int end = Math.min((page + 1) * size, all.size());
        List<Found> pageList = all.subList(start, end);

        model.addAttribute("foundList", pageList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) all.size() / size));
        model.addAttribute("city", city);
        model.addAttribute("type", type);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "found-list";
    }

    @GetMapping("/matches")
    public String showMatchList(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            Model model) {

        List<FaceMatchRecord> matches = faceMatchRepo.findAll();

        Comparator<FaceMatchRecord> comparator = Comparator.comparing(FaceMatchRecord::getMatchDate);
        if ("status".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(FaceMatchRecord::getStatus, Comparator.nullsLast(String::compareToIgnoreCase));
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        matches.sort(comparator);

        model.addAttribute("matches", matches);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "match-list";
    }

    private String runFaceRecognitionScript(String imagePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Users\\kamel\\AppData\\Local\\Programs\\Python\\Python310\\python.exe",
                    "C:\\kamel\\alkwebsite\\missingloved\\uploads\\match_faces.py",
                    imagePath
            );

            pb.directory(new File("C:\\kamel\\alkwebsite\\missingloved\\uploads"));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("PYTHON: " + line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("PYTHON EXIT CODE: " + exitCode);
            return output.toString();

        } catch (Exception e) {
            System.err.println("Error running face recognition: " + e.getMessage());
            return null;
        }
    }
}
