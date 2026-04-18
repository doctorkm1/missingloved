package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class SightUsrController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/sightusr")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "sightusr";
    }

    @PostMapping("/sightusr")
    public String submitForm(@ModelAttribute("user") User user,
                             Model model,
                             HttpServletRequest request) {

        user.setCreatedAt(LocalDateTime.now());
        user.setcreatedIp(request.getRemoteAddr());
        user.setAuthorityCode(0);
//        user.setCreatedBy("self");

        Object loggedUser = request.getSession().getAttribute("loggedUser");
        if (loggedUser instanceof User loggedInUser) {
            user.setCreatedBy(loggedInUser.getUserCode());  // logged-in user code
        } else {
            user.setCreatedBy(user.getUserCode());          // fallback: own code
        }
        userRepository.save(user);
        model.addAttribute("successMessage", "تم التسجيل بنجاح , إضغط إستمرار لتسجيل بيانات الحالة");
        model.addAttribute("user", user);
        return "sightusr";
    }
}
