package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // === SIGNUP FORM ===
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        if (!model.containsAttribute("user")) {
            User user = new User();
            user.setCountry("مصر");
            model.addAttribute("user", user);
        }
        model.addAttribute("forceErrors", true);
        return "signup";
    }

    // === SIGNUP SUBMIT ===
    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") User user,
                                BindingResult bindingResult,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        System.out.println("📥 Signup request for: " + user.getUserCode());

        // Validate uniqueness
        if (userService.existsByUserCode(user.getUserCode())) {
            bindingResult.rejectValue("userCode", "error.user", "اسم المستخدم مستخدم بالفعل");
        }
        if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "البريد الإلكتروني مستخدم بالفعل");
        }
        if (userService.existsByNationalId(user.getNationalId())) {
            bindingResult.rejectValue("nationalId", "error.user", "الرقم القومي مستخدم بالفعل");
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(e -> {
                System.out.println("❗ Field error: " + e.getField() + " => " + e.getDefaultMessage());
            });

            System.out.println("⚠️ Validation errors during signup.");
            model.addAttribute("forceErrors", true);
            return "signup";
        }

        try {
            // Setup fields
            user.setAuthorityCode(0);
            user.setActivated(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setcreatedIp(request.getRemoteAddr());

            Object sessionUser = request.getSession().getAttribute("userCode");
            user.setCreatedBy(sessionUser != null ? sessionUser.toString() : "anonymous");
// Set metadata before saving
            user.setCreatedAt(java.time.LocalDateTime.now());

            String clientIp = request.getRemoteAddr();
            user.setcreatedIp(clientIp);

            Object loggedUser = request.getSession().getAttribute("loggedUser");
            if (loggedUser instanceof com.alkamel.missingloved.model.User loggedInUser) {
                System.out.println("✅ Logged-in user creating record. userCode = " + loggedInUser.getUserCode());
                user.setCreatedBy(loggedInUser.getUserCode());
            } else {
                System.out.println("⚠️ No logged-in user found in session. Using self userCode = " + user.getUserCode());
                user.setCreatedBy(user.getUserCode());
            }
            userService.createUser(user);

            System.out.println("✅ User saved: " + user.getUserCode());

            redirectAttributes.addFlashAttribute("successMessage", "✅ تم التسجيل بنجاح! يرجى انتظار التفعيل.");
            return "redirect:/signup";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ حدث خطأ أثناء حفظ المستخدم: " + e.getMessage());
            System.out.println("❌ Exception while saving user: " + e.getMessage());
            return "signup";
        }
    }

    // === SIGNIN ===
    @PostMapping("/signin")
    public String processSignin(@RequestParam("userCode") String userCode,
                                @RequestParam("password") String password,
                                HttpServletRequest request,
                                Model model) {

        Optional<User> optionalUser = userService.getUserByCode(userCode);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.getPassword().equals(password)) {
                model.addAttribute("loginError", "⚠️ كود المستخدم أو كلمة المرور غير صحيحة");
                return "login";
            }

            if (user.getAuthorityCode() == null || user.getAuthorityCode() == 0) {
                model.addAttribute("loginError", "⚠️ الحساب غير مفعل بعد. برجاء التواصل مع الإدارة.");
                return "login";
            }
            request.getSession().setAttribute("userCode", user.getUserCode());
            request.getSession().setAttribute("loggedInUser", user);
            request.getSession().setAttribute("loggedUser", user); // ✅ ensures createdBy picks this up
           System.out.println("✅ [DEBUG] Session user set (via request): " + user.getUserCode());
            return "redirect:/";
        }

        model.addAttribute("loginError", "⚠️ اسم المستخدم غير صحيح");
        return "login";
    }

    // === AJAX: Check if username exists ===
    @GetMapping("/api/check-username")
    @ResponseBody
    public Boolean checkUsernameAvailability(@RequestParam("username") String username) {
        return userService.existsByUserCode(username);
    }
}