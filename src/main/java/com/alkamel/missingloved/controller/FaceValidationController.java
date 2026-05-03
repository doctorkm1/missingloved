package com.alkamel.missingloved.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FaceValidationController {

    // ✔ keep everything inside your production structure
    private static final String BASE_DIR = "C:\\kamel\\alkwebsite\\missingloved\\uploads\\";
    private static final String TEMP_DIR = BASE_DIR + "temp_face_validation\\";
    private static final String PYTHON_SCRIPT = BASE_DIR + "detect_face_count.py";

    @PostMapping(value = "/api/validate-face", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> validateFace(@RequestParam("file") MultipartFile file) {

        Map<String, Object> result = new HashMap<>();
        int faceCount = 0;

        try {
            // ==============================
            // 1. Ensure temp directory exists
            // ==============================
            File dir = new File(TEMP_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.out.println("Warning: temp directory not created: " + TEMP_DIR);
                }
            }

            // ==============================
            // 2. Save uploaded file
            // ==============================
            String tempFilePath = TEMP_DIR + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Path.of(tempFilePath);
            Files.write(path, file.getBytes());

            // ==============================
            // 3. Call Python script
            // ==============================
            ProcessBuilder pb = new ProcessBuilder(
                    "python",
                    PYTHON_SCRIPT,
                    tempFilePath
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    faceCount = Integer.parseInt(line.trim());
                } catch (Exception ignored) {
                    // ignore non-numeric lines
                }
            }

            process.waitFor();

            // ==============================
            // 4. Cleanup
            // ==============================
            Files.deleteIfExists(path);

        } catch (Exception e) {
            System.out.println("Face validation error: " + e.getMessage());
            faceCount = 0;
        }

        // ==============================
        // 5. Return result
        // ==============================
        result.put("face_count", faceCount);
        return result;
    }
}