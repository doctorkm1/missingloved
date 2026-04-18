package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.MissingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class AdminUtilityController {

    private final MissingRepository missingRepository;

    public AdminUtilityController(MissingRepository missingRepository) {
        this.missingRepository = missingRepository;
    }

    @GetMapping("/admin/export-missing-photo-map")
    @ResponseBody
    public String exportMissingImageMap() {
        String csvPath = "C:/kamel/alkwebsite/missingloved/missing_image_map.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("photo_file_name,missing_id");

            List<Missing> all = missingRepository.findAll();
            for (Missing m : all) {
                if (m.getPhotoFileName() != null && !m.getPhotoFileName().isBlank()) {
                    writer.printf("%s,%d%n", m.getPhotoFileName(), m.getId());
                }
            }

            return "✅ تم إنشاء الملف بنجاح: " + csvPath;

        } catch (IOException e) {
            return "❌ فشل في إنشاء الملف: " + e.getMessage();
        }
    }
}
