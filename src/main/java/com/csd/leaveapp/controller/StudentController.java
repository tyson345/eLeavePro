package com.csd.leaveapp.controller;

import com.csd.leaveapp.model.Role;
import com.csd.leaveapp.service.AppService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class StudentController {
    private final AppService appService;

    public StudentController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/student/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!loadStudentModel(session, model)) {
            return "redirect:/login";
        }
        return "student-home";
    }

    // Common typo safeguard (prevents "Not Found" if user types URL manually)
    @GetMapping("/student/dashbaord")
    public String dashboardTypoRedirect() {
        return "redirect:/student/dashboard";
    }

    @GetMapping("/student/leave")
    public String leavePage(HttpSession session, Model model) {
        if (!loadStudentModel(session, model)) {
            return "redirect:/login";
        }
        return "student-leave";
    }

    @GetMapping("/student/marks")
    public String marksPage(HttpSession session, Model model) {
        if (!loadStudentModel(session, model)) {
            return "redirect:/login";
        }
        return "student-marks";
    }

    @PostMapping("/student/apply-leave")
    public String applyLeave(HttpSession session,
                             @RequestParam String fromDate,
                             @RequestParam String toDate,
                             @RequestParam String reason,
                             Model model) {
        if (!isStudent(session)) {
            return "redirect:/login";
        }
        String usn = (String) session.getAttribute("usn");
        String error = appService.applyLeave(usn, LocalDate.parse(fromDate), LocalDate.parse(toDate), reason);
        if (error != null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", "Leave request submitted to HOD.");
        }
        return leavePage(session, model);
    }

    private boolean isStudent(HttpSession session) {
        return Role.STUDENT.name().equals(session.getAttribute("role"));
    }

    private boolean loadStudentModel(HttpSession session, Model model) {
        if (!isStudent(session)) {
            return false;
        }
        String usn = (String) session.getAttribute("usn");
        if (appService.findUser(usn).isEmpty()) {
            return false;
        }
        Map<String, Object> report = appService.studentReport(usn);
        model.addAttribute("student", report.get("student"));
        model.addAttribute("requests", report.get("requests"));
        model.addAttribute("decisionMessage", report.get("decisionMessage"));
        model.addAttribute("usedLeaves", report.get("usedLeaves"));
        model.addAttribute("studentTrend", report.get("studentTrend"));
        return true;
    }
}
