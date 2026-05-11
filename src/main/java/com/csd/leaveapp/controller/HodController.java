package com.csd.leaveapp.controller;

import com.csd.leaveapp.model.LeaveStatus;
import com.csd.leaveapp.model.Role;
import com.csd.leaveapp.service.AppService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HodController {
    private final AppService appService;

    public HodController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/hod/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        return "hod-home";
    }

    // Common typo safeguard (prevents "Not Found" if user types URL manually)
    @GetMapping("/hod/dashbaord")
    public String dashboardTypoRedirect() {
        return "redirect:/hod/dashboard";
    }

    @GetMapping("/hod/leaves")
    public String leavesPage(HttpSession session, Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        return "hod-leaves";
    }

    @GetMapping("/hod/students")
    public String studentsPage(HttpSession session, Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        return "hod-students";
    }

    @GetMapping("/hod/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        model.addAttribute("selectedSemester", 8);
        model.addAttribute("semesterRows", appService.getSemesterRecords(8));
        model.addAttribute("deptTrend", appService.getDepartmentTrendReport());
        return "hod-reports";
    }

    @GetMapping("/hod/semester-records")
    public String semesterRecords(HttpSession session,
                                  @RequestParam int semester,
                                  Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("semesterRows", appService.getSemesterRecords(semester));
        model.addAttribute("deptTrend", appService.getDepartmentTrendReport());
        return "hod-reports";
    }

    @GetMapping("/hod/student-progress")
    public String individualProgress(HttpSession session, @RequestParam String usn, Model model) {
        if (!loadCommonData(session, model)) {
            return "redirect:/login";
        }
        appService.findUser(usn).ifPresent(student -> {
            model.addAttribute("selectedStudent", student);
            model.addAttribute("selectedStudentTrend", appService.getStudentTrendReport(usn));
        });
        model.addAttribute("selectedUsn", usn);
        return "hod-student-progress";
    }

    @PostMapping("/hod/student/marks")
    public String updateStudentMarks(HttpSession session,
                                     @RequestParam String usn,
                                     @RequestParam int semester,
                                     @RequestParam int totalMarks,
                                     @RequestParam int acquiredMarks,
                                     Model model) {
        if (!isHod(session)) {
            return "redirect:/login";
        }
        String error = appService.updateStudentMarks(usn, semester, totalMarks, acquiredMarks);
        model.addAttribute("selectedUsn", usn);
        if (error != null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", "Marks updated by HOD.");
        }
        return studentsPage(session, model);
    }

    @PostMapping("/hod/leave/decision")
    public String decideLeave(HttpSession session,
                              @RequestParam long leaveId,
                              @RequestParam String decision,
                              Model model) {
        if (!isHod(session)) {
            return "redirect:/login";
        }
        LeaveStatus status = "APPROVE".equals(decision) ? LeaveStatus.APPROVED : LeaveStatus.REJECTED;
        String error = appService.decideLeave(leaveId, status);
        if (error != null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", "Leave request " + status.name().toLowerCase() + ".");
        }
        return leavesPage(session, model);
    }

    @PostMapping("/hod/student/update")
    public String updateStudent(HttpSession session,
                                @RequestParam String usn,
                                @RequestParam String name,
                                @RequestParam int semester,
                                @RequestParam double cgpa,
                                @RequestParam int leaveBalance,
                                Model model) {
        if (!isHod(session)) {
            return "redirect:/login";
        }
        String error = appService.updateStudent(usn, name, semester, cgpa, leaveBalance);
        model.addAttribute("selectedUsn", usn);
        if (error != null) {
            model.addAttribute("error", error);
        } else {
            model.addAttribute("success", "Student profile updated.");
        }
        return studentsPage(session, model);
    }

    private boolean loadCommonData(HttpSession session, Model model) {
        if (!isHod(session)) {
            return false;
        }
        model.addAttribute("requests", appService.getAllRequests());
        model.addAttribute("students", appService.getAllStudents());
        model.addAttribute("report", appService.departmentReport());
        return true;
    }

    private boolean isHod(HttpSession session) {
        return Role.HOD.name().equals(session.getAttribute("role"));
    }
}
