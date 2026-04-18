package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Missing;
import com.alkamel.missingloved.repository.MissingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private MissingRepository missingRepository;

    @GetMapping("/safe-space-info")
    public String showSafeSpaceInfoPage() {
        return "safe-space-info"; // اسم الملف بدون .html
    }


    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 12;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Missing> activeMissingPage = missingRepository.findByActivatedTrue(pageable);

        List<Missing> activeMissings = activeMissingPage.getContent();

        model.addAttribute("activeMissings", activeMissings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", activeMissingPage.getTotalPages());

        return "home";
    }
}
