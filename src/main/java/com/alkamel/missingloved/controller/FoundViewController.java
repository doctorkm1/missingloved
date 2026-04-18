package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.Found;
import com.alkamel.missingloved.repository.FoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/found")
public class FoundViewController {

    @Autowired
    private FoundRepository foundRepository;

//    @GetMapping("/view/{id}")
 //   public String viewFound(@PathVariable Integer id, Model model) {
 //       Found found = foundRepository.findById(id).orElse(null);
 //       if (found == null) {
 //           model.addAttribute("errorMessage", "لم يتم العثور على الحالة المطلوبة.");
 //           return "error";
 //       }
//
 //        model.addAttribute("found", found);
 //       return "found-view";
 //   }
}
