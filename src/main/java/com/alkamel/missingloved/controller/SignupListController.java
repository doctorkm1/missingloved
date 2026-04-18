package com.alkamel.missingloved.controller;

import com.alkamel.missingloved.model.User;
import com.alkamel.missingloved.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/signup-list")
public class SignupListController {

    @Autowired
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String showAllUsers(@RequestParam(required = false) Integer authorityCode,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {

        List<User> filteredUsers = userService.getAllUsers().stream()
                .filter(u -> {
                    if (authorityCode != null && (u.getAuthorityCode() == null || !u.getAuthorityCode().equals(authorityCode)))
                        return false;
                    if (keyword != null && !keyword.isBlank()) {
                        String kw = keyword.toLowerCase();
                        return (u.getUserCode() != null && u.getUserCode().toLowerCase().contains(kw)) ||
                                (u.getEmail() != null && u.getEmail().toLowerCase().contains(kw)) ||
                                (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(kw)) ||
                                (u.getFamilyName() != null && u.getFamilyName().toLowerCase().contains(kw));
                    }
                    return true;
                })
                .sorted(Comparator.comparing(User::getId).reversed())
                .collect(Collectors.toList());

        int total = filteredUsers.size();
        int fromIndex = Math.min(page * PAGE_SIZE, total);
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);

        List<User> pageList = filteredUsers.subList(fromIndex, toIndex);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        model.addAttribute("userList", pageList);
        model.addAttribute("authorityCode", authorityCode);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "signup-list";
    }

    @PostMapping("/update/{id}")
    public String updateUserAuthority(@PathVariable Long id,
                                      @RequestParam("authorityCode") Integer authorityCode,
                                      HttpServletRequest request) {

        String adminCode = (String) request.getSession().getAttribute("userCode");
        String ip = request.getRemoteAddr();
        System.out.println("Admin user code from session: " + adminCode);

        userService.getUserById(id).ifPresent(user -> {
            user.setAuthorityCode(authorityCode);

            if (authorityCode != null && authorityCode > 0) {
                user.setActivatedBy(adminCode);
                user.setActivatedAt(LocalDateTime.now());
                user.setActivationIp(ip);
            } else {
                user.setDeactivatedBy(adminCode);
                user.setDeactivatedAt(LocalDateTime.now());
                user.setDeactivationIp(ip);
            }

            userService.updateUser(id, user);
        });

        return "redirect:/admin/signup-list";
    }
}
