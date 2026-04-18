package com.alkamel.missingloved.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    // Show login page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login"; // login.html
    }

/*    // Handle login post   COMMENTED AT 22 06 2025  AS THERE ARE ANOTHER CODE AT AUTHCONTROLLER.JAVA
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User loginUser,
                               Model model,
                               HttpSession session) {
        User user = userService.findByUserCode(loginUser.getUserCode());

        if (user == null) {
            model.addAttribute("error", "رمز المستخدم غير موجود");
            return "login";
        }

        if (!user.getPassword().equals(loginUser.getPassword())) {
            model.addAttribute("error", "كلمة المرور غير صحيحة");
            return "login";
        }

        // CAPTCHA check should be added here if implemented

        // Save user info in session
        session.setAttribute("loggedInUser", user);
        session.setAttribute("loggedUserCode", user.getUserCode());  // <-- add this line
        session.setAttribute("loggedInUser", user);
        System.out.println("🟢 [Login] User stored in session: " + user.getUserCode());
        return "redirect:/"; // or redirect to the original page
    }
*/      //    -------------------------------------------------
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/api/logged-user")
    @ResponseBody
    public Map<String, String> getLoggedUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Map<String, String> info = new HashMap<>();
        if (user != null) {
            System.out.println("✅ [DEBUG] Session user = " + user.getUserCode() + ", " + user.getFirstName());
            info.put("userCode", user.getUserCode());
            info.put("firstName", user.getFirstName());
        } else {
            System.out.println("⚠️ [DEBUG] No user in session.");
        }
        return info;
    }
}