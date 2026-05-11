package com.csd.leaveapp.controller;

import com.csd.leaveapp.model.Role;
import com.csd.leaveapp.model.User;
import com.csd.leaveapp.service.AppService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {
    private final AppService appService;

    public AuthController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usn,
                        @RequestParam String password,
                        @RequestParam String role,
                        HttpServletRequest request,
                        HttpSession session,
                        Model model) {
        Role selectedRole;
        try {
            selectedRole = Role.valueOf(role);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Invalid role selected.");
            return "login";
        }

        String normalizedUsn = usn == null ? "" : usn.trim().toUpperCase();
        Optional<User> user = appService.login(normalizedUsn, password, selectedRole);
        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid credentials or role.");
            return "login";
        }

        // Rotate session on successful login to prevent session fixation / role "bleed" across logins.
        session.invalidate();
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("usn", user.get().getUsn());
        newSession.setAttribute("role", user.get().getRole().name());
        return user.get().getRole() == Role.HOD ? "redirect:/hod/dashboard" : "redirect:/student/dashboard";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String usn,
                         @RequestParam String password,
                         Model model) {
        String error = appService.registerStudent(usn, password);
        if (error != null) {
            model.addAttribute("error", error);
            return "signup";
        }
        model.addAttribute("success", "Account activated successfully. Please login.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        session.invalidate();
        return "redirect:/login";
    }
}
