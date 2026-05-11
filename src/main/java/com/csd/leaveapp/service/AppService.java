package com.csd.leaveapp.service;

import com.csd.leaveapp.model.LeaveRequest;
import com.csd.leaveapp.model.LeaveStatus;
import com.csd.leaveapp.model.Role;
import com.csd.leaveapp.model.SemesterRecord;
import com.csd.leaveapp.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AppService {
    private static final int MAX_LEAVE_DAYS = 10;
    private static final double CHART_MIN_SGPA = 5.0;
    private static final double CHART_MAX_SGPA = 10.0;
    private static final int DEFAULT_TOTAL_MARKS = 900;
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<Long, LeaveRequest> leaveRequests = new ConcurrentHashMap<>();
    private final AtomicLong leaveSequence = new AtomicLong(1);

    public AppService() {
        seedData();
    }

    private void seedData() {
        users.put("CSDHOD001", new User("CSDHOD001", "Dr. CSD HOD", "hod123", Role.HOD, 0, 0, 0));
        addStudent("4PM22CG001", "ADITYA S");
        addStudent("4PM22CG002", "AIMAN BIAG");
        addStudent("4PM22CG003", "AKSHAY V");
        addStudent("4PM22CG004", "ANANYA K");
        addStudent("4PM22CG005", "ARPITHA R");
        addStudent("4PM22CG006", "ARPITHA S");
        addStudent("4PM22CG007", "BHOOMIKA R");
        addStudent("4PM22CG009", "BI BI MARIYAM");
        addStudent("4PM22CG010", "CHANDANA C S");
        addStudent("4PM22CG012", "DARSHAN S");
        addStudent("4PM22CG013", "DEEKSHTHITA D N");
        addStudent("4PM22CG014", "DEEKSITHA R");
        addStudent("4PM22CG015", "GOPIKA E L");
        addStudent("4PM22CG016", "HAFSA NOORAIN AMJED");
        addStudent("4PM22CG017", "HASAN RAZA");
        addStudent("4PM22CG018", "HITHAISHI U");
        addStudent("4PM22CG019", "JAYANTH A YADAV");
        addStudent("4PM22CG020", "KARTHIK GOPAL MADIVALA");
        addStudent("4PM22CG021", "KARTHIK K R");
        addStudent("4PM22CG022", "KRUTHIKA B I");
        addStudent("4PM22CG023", "MANJU MADHAV V A");
        addStudent("4PM22CG024", "MEGHANA K M");
        addStudent("4PM22CG025", "NANDHITHA G P");
        addStudent("4PM22CG027", "NISHANT");
        addStudent("4PM22CG028", "NISHANTH K R");
        addStudent("4PM22CG029", "NITHIN A B");
        addStudent("4PM22CG030", "NIVEDIA SHANKAR GOUDA");
        addStudent("4PM22CG031", "PADMINI V");
        addStudent("4PM22CG032", "POORNASREE S V");
        addStudent("4PM22CG033", "PRATHAMESHA C SHETTY");
        addStudent("4PM22CG034", "PRERANA ASHOKA RAYKAR");
        addStudent("4PM22CG035", "PRIYA Y M");
        addStudent("4PM22CG036", "ROHAN K RAJOLI");
        addStudent("4PM22CG037", "SACHIN K");
        addStudent("4PM22CG038", "SAKSHI S Y");
        addStudent("4PM22CG039", "SANJANA N A");
        addStudent("4PM22CG040", "SHADABUR RAHAMAN");
        addStudent("4PM22CG041", "SHAMANTH S KUMBAR");
        addStudent("4PM22CG042", "SHANKAR");
        addStudent("4PM22CG043", "SHASHANK V S");
        addStudent("4PM22CG044", "SHIVASHANKAR AJIT AWATE");
        addStudent("4PM22CG045", "SHREYA JANARDHAN MADIVAL");
        addStudent("4PM22CG046", "SINCHANAN S");
        addStudent("4PM22CG047", "SOURABH PATIL");
        addStudent("4PM22CG048", "SRUSHTI G P");
        addStudent("4PM22CG049", "SRUSHTI N Y");
        addStudent("4PM22CG050", "SUBHASH CHANDRA KOWSHIK H S");
        addStudent("4PM22CG051", "SUPRITHA G C");
        addStudent("4PM22CG052", "SURAJ V");
        addStudent("4PM22CG053", "SUSHMA K V");
        addStudent("4PM22CG054", "SUSHMITHA M J");
        addStudent("4PM22CG055", "THANUJA C N");
        addStudent("4PM22CG056", "THOOFIK USMAAN A");
        addStudent("4PM22CG057", "U GOUTHAM KRISHNA");
        addStudent("4PM22CG058", "UDAY P");
        addStudent("4PM22CG059", "VAISHNAVI G K");
        addStudent("4PM22CG060", "VANISHREE M");
        addStudent("4PM22CG061", "VARSHA S");
    }

    private void addStudent(String usn, String name) {
        List<SemesterRecord> records = new ArrayList<>();
        int seed = Math.abs(Objects.hash(usn, name));
        double cgpaAccumulator = 0.0;
        int completedSems = 0;
        double previousSgpa = 5.9 + ((seed % 160) / 100.0); // 5.9 - 7.49 baseline

        for (int sem = 1; sem <= 8; sem++) {
            double longTrend = (((seed / 17) % 9) - 4) * 0.03;
            double wave = (((seed + sem * sem * 13) % 11) - 5) * 0.07;
            double semStep = 0.12 + longTrend + wave;
            double sgpa = Math.max(5.8, Math.min(9.9, previousSgpa + semStep));
            previousSgpa = sgpa;

            int acquiredJitter = ((seed + sem * 23) % 31) - 15;
            int acquired = (int) Math.max(0, Math.min(DEFAULT_TOTAL_MARKS, Math.round((sgpa / 10.0) * DEFAULT_TOTAL_MARKS) + acquiredJitter));
            records.add(new SemesterRecord(sem, sgpa, DEFAULT_TOTAL_MARKS, acquired));
            cgpaAccumulator += sgpa;
            completedSems++;
        }

        double cgpa = completedSems == 0 ? 0.0 : cgpaAccumulator / completedSems;
        User student = new User(usn, name, "student123", Role.STUDENT, cgpa, 8, MAX_LEAVE_DAYS);
        student.setSemesterRecords(records);
        users.put(usn, student);
    }

    public Optional<User> login(String usn, String password, Role role) {
        User user = users.get(usn);
        if (user == null || user.getRole() != role) {
            return Optional.empty();
        }
        if (!user.isActive()) {
            return Optional.empty();
        }
        return Objects.equals(user.getPassword(), password) ? Optional.of(user) : Optional.empty();
    }

    public String registerStudent(String usn, String password) {
        User student = users.get(usn);
        if (student == null || student.getRole() != Role.STUDENT) {
            return "USN not found in department list.";
        }
        student.setPassword(password);
        student.setActive(true);
        return null;
    }

    public Optional<User> findUser(String usn) {
        return Optional.ofNullable(users.get(usn));
    }

    public String applyLeave(String studentUsn, LocalDate fromDate, LocalDate toDate, String reason) {
        User student = users.get(studentUsn);
        if (student == null || student.getRole() != Role.STUDENT) {
            return "Student not found.";
        }
        if (fromDate == null || toDate == null || toDate.isBefore(fromDate)) {
            return "Invalid leave dates.";
        }
        int days = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        if (days <= 0) {
            return "Invalid leave duration.";
        }
        if (days > MAX_LEAVE_DAYS) {
            return "Single leave request cannot exceed 10 days.";
        }
        if (student.getLeaveBalance() < days) {
            return "Not enough leave balance.";
        }

        long id = leaveSequence.getAndIncrement();
        LeaveRequest request = new LeaveRequest(id, student.getUsn(), student.getName(), fromDate, toDate, reason, days, LeaveStatus.PENDING);
        leaveRequests.put(id, request);
        return null;
    }

    public List<LeaveRequest> getStudentRequests(String studentUsn) {
        return leaveRequests.values().stream()
                .filter(request -> request.getStudentUsn().equals(studentUsn))
                .sorted(Comparator.comparing(LeaveRequest::getId).reversed())
                .toList();
    }

    public List<LeaveRequest> getAllRequests() {
        return leaveRequests.values().stream()
                .sorted(Comparator.comparing(LeaveRequest::getId).reversed())
                .toList();
    }

    public String decideLeave(long leaveId, LeaveStatus status) {
        LeaveRequest request = leaveRequests.get(leaveId);
        if (request == null) {
            return "Leave request not found.";
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            return "This request was already processed.";
        }

        if (status == LeaveStatus.APPROVED) {
            User student = users.get(request.getStudentUsn());
            if (student == null) {
                return "Student not found.";
            }
            if (student.getLeaveBalance() < request.getNumberOfDays()) {
                return "Student does not have enough leave balance.";
            }
            student.setLeaveBalance(student.getLeaveBalance() - request.getNumberOfDays());
        }

        request.setStatus(status);
        return null;
    }

    public List<User> getAllStudents() {
        return users.values().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .sorted(Comparator.comparing(User::getUsn))
                .toList();
    }

    public String updateStudent(String usn, String name, int semester, double cgpa, int leaveBalance) {
        User student = users.get(usn);
        if (student == null || student.getRole() != Role.STUDENT) {
            return "Student not found.";
        }
        student.setName(name);
        student.setSemester(semester);
        student.setCgpa(cgpa);
        student.setLeaveBalance(Math.min(Math.max(leaveBalance, 0), MAX_LEAVE_DAYS));
        return null;
    }

    public String updateStudentMarks(String usn, int semester, int totalMarks, int acquiredMarks) {
        User student = users.get(usn);
        if (student == null || student.getRole() != Role.STUDENT) {
            return "Student not found.";
        }
        if (semester < 1 || semester > 8) {
            return "Semester must be between 1 and 8.";
        }
        if (totalMarks <= 0) {
            return "Total marks must be greater than 0.";
        }
        if (acquiredMarks < 0 || acquiredMarks > totalMarks) {
            return "Acquired marks must be between 0 and total marks.";
        }
        SemesterRecord record = student.getSemesterRecords().get(semester - 1);
        record.setTotalMarks(totalMarks);
        record.setAcquiredMarks(acquiredMarks);
        double sgpa = Math.round(((acquiredMarks * 10.0) / totalMarks) * 100.0) / 100.0;
        record.setSgpa(sgpa);
        student.setSemester(Math.max(student.getSemester(), semester));

        double avg = student.getSemesterRecords().stream()
                .filter(r -> r.getSgpa() > 0)
                .mapToDouble(SemesterRecord::getSgpa)
                .average()
                .orElse(0.0);
        student.setCgpa(avg);
        return null;
    }

    public Map<String, Object> departmentReport() {
        List<User> students = getAllStudents();
        List<LeaveRequest> requests = new ArrayList<>(leaveRequests.values());

        double avgCgpa = students.stream().mapToDouble(User::getCgpa).average().orElse(0.0);
        long approved = requests.stream().filter(r -> r.getStatus() == LeaveStatus.APPROVED).count();
        long pending = requests.stream().filter(r -> r.getStatus() == LeaveStatus.PENDING).count();
        long rejected = requests.stream().filter(r -> r.getStatus() == LeaveStatus.REJECTED).count();
        long totalDecisions = Math.max(1, approved + pending + rejected);
        int approvedPct = (int) Math.round((approved * 100.0) / totalDecisions);
        int pendingPct = (int) Math.round((pending * 100.0) / totalDecisions);
        int totalLeavesLeft = students.stream().mapToInt(User::getLeaveBalance).sum();

        Map<String, Object> report = new ConcurrentHashMap<>();
        report.put("studentCount", students.size());
        report.put("avgCgpa", String.format("%.2f", avgCgpa));
        report.put("approvedLeaves", approved);
        report.put("pendingLeaves", pending);
        report.put("rejectedLeaves", rejected);
        report.put("approvedPct", approvedPct);
        report.put("pendingPct", pendingPct);
        report.put("rejectedPct", Math.max(0, 100 - approvedPct - pendingPct));
        report.put("totalLeavesLeft", totalLeavesLeft);
        report.put("pendingNotification", pending > 0 ? "You have " + pending + " new leave request(s)." : "No new leave requests.");
        report.put("topperUsn", students.stream().max(Comparator.comparing(User::getCgpa)).map(User::getUsn).orElse("-"));
        return report;
    }

    public List<Map<String, Object>> getSemesterRecords(int semester) {
        List<Map<String, Object>> data = new ArrayList<>();
        if (semester < 1 || semester > 8) {
            return data;
        }
        for (User student : getAllStudents()) {
            SemesterRecord semRecord = student.getSemesterRecords().get(semester - 1);
            Map<String, Object> row = new HashMap<>();
            row.put("usn", student.getUsn());
            row.put("name", student.getName());
            row.put("totalMarks", semRecord.getTotalMarks());
            row.put("acquiredMarks", semRecord.getAcquiredMarks());
            row.put("sgpa", semRecord.getSgpa());
            row.put("cgpa", student.getCgpa());
            data.add(row);
        }
        return data;
    }

    public Map<String, Object> getDepartmentTrendReport() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> semesterSummary = new ArrayList<>();
        List<Double> avgSgpas = new ArrayList<>();

        double min = 10.0;
        double max = 0.0;
        for (int sem = 1; sem <= 8; sem++) {
            int studentCount = 0;
            double sgpaSum = 0.0;
            int totalMarksSum = 0;
            int acquiredMarksSum = 0;
            for (User student : getAllStudents()) {
                SemesterRecord record = student.getSemesterRecords().get(sem - 1);
                sgpaSum += record.getSgpa();
                totalMarksSum += record.getTotalMarks();
                acquiredMarksSum += record.getAcquiredMarks();
                studentCount++;
            }
            double avgSgpa = studentCount == 0 ? 0.0 : sgpaSum / studentCount;
            double avgTotalMarks = studentCount == 0 ? 0.0 : totalMarksSum / (double) studentCount;
            double avgAcquiredMarks = studentCount == 0 ? 0.0 : acquiredMarksSum / (double) studentCount;
            avgSgpas.add(avgSgpa);
            min = Math.min(min, avgSgpa);
            max = Math.max(max, avgSgpa);

            Map<String, Object> row = new HashMap<>();
            row.put("semester", sem);
            row.put("avgSgpa", avgSgpa);
            row.put("avgTotalMarks", avgTotalMarks);
            row.put("avgAcquiredMarks", avgAcquiredMarks);
            if (sem == 1) {
                row.put("trend", "BASE");
                row.put("delta", 0.0);
            } else {
                double delta = avgSgpa - avgSgpas.get(sem - 2);
                row.put("trend", delta > 0.01 ? "UP" : delta < -0.01 ? "DOWN" : "SAME");
                row.put("delta", delta);
            }
            semesterSummary.add(row);
        }

        result.putAll(buildLineChartData(avgSgpas, 620, 260, 20, 220));
        result.put("semesterSummary", semesterSummary);
        result.put("firstSemAvg", avgSgpas.get(0));
        result.put("lastSemAvg", avgSgpas.get(avgSgpas.size() - 1));
        result.put("overallDelta", avgSgpas.get(avgSgpas.size() - 1) - avgSgpas.get(0));
        return result;
    }

    public Map<String, Object> studentReport(String usn) {
        Map<String, Object> data = new HashMap<>();
        User student = users.get(usn);
        if (student == null) {
            return data;
        }
        data.put("student", student);
        data.put("requests", getStudentRequests(usn));
        data.put("decisionMessage", getStudentDecisionMessage(usn));
        data.put("usedLeaves", MAX_LEAVE_DAYS - student.getLeaveBalance());
        data.put("studentTrend", getStudentTrendReport(usn));
        return data;
    }

    public Map<String, Object> getStudentTrendReport(String usn) {
        Map<String, Object> trend = new HashMap<>();
        User student = users.get(usn);
        if (student == null || student.getRole() != Role.STUDENT) {
            return trend;
        }

        List<SemesterRecord> records = student.getSemesterRecords();
        List<Double> sgpas = records.stream().map(SemesterRecord::getSgpa).toList();
        trend.putAll(buildLineChartData(sgpas, 620, 260, 20, 220));

        List<Map<String, Object>> details = new ArrayList<>();
        double runningTotal = 0.0;
        for (int i = 0; i < records.size(); i++) {
            SemesterRecord r = records.get(i);
            runningTotal += r.getSgpa();
            double runningCgpa = runningTotal / (i + 1);
            double delta = i == 0 ? 0.0 : r.getSgpa() - records.get(i - 1).getSgpa();
            Map<String, Object> row = new HashMap<>();
            row.put("semester", r.getSemester());
            row.put("sgpa", r.getSgpa());
            row.put("totalMarks", r.getTotalMarks());
            row.put("acquiredMarks", r.getAcquiredMarks());
            row.put("runningCgpa", runningCgpa);
            row.put("delta", delta);
            row.put("trend", i == 0 ? "BASE" : (delta > 0.01 ? "UP" : (delta < -0.01 ? "DOWN" : "SAME")));
            details.add(row);
        }
        trend.put("semesterDetails", details);
        trend.put("firstSemSgpa", records.get(0).getSgpa());
        trend.put("lastSemSgpa", records.get(records.size() - 1).getSgpa());
        trend.put("overallDelta", records.get(records.size() - 1).getSgpa() - records.get(0).getSgpa());
        return trend;
    }

    private String buildPolylinePoints(List<Double> values, int width, int height, int leftPadding, int bottomY) {
        if (values.isEmpty()) {
            return "";
        }
        double min = CHART_MIN_SGPA;
        double max = CHART_MAX_SGPA;
        double range = max - min;
        double rightPadding = leftPadding;
        double xGap = values.size() == 1 ? 0 : (double) (width - leftPadding - rightPadding) / (values.size() - 1);
        // Align with the template axis: y1="30" to y2="${bottomY}".
        double chartTopY = Math.max(20.0, height * 0.12);
        double chartHeight = bottomY - chartTopY;

        StringBuilder points = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            double x = leftPadding + (i * xGap);
            double normalized = (Math.max(min, Math.min(max, values.get(i))) - min) / range;
            double y = bottomY - (normalized * chartHeight);
            // Avoid truncation; small SGPA changes must be visible as smooth trend movement.
            points.append(String.format(Locale.US, "%.2f", x)).append(",").append(String.format(Locale.US, "%.2f", y));
            if (i < values.size() - 1) {
                points.append(" ");
            }
        }
        return points.toString();
    }

    private Map<String, Object> buildLineChartData(List<Double> values, int width, int height, int leftPadding, int bottomY) {
        Map<String, Object> chart = new HashMap<>();
        chart.put("linePoints", buildPolylinePoints(values, width, height, leftPadding, bottomY));

        if (values.isEmpty()) {
            chart.put("plotPoints", List.of());
            chart.put("yAxisTicks", List.of());
            return chart;
        }

        double min = CHART_MIN_SGPA;
        double max = CHART_MAX_SGPA;
        double range = max - min;
        double chartTopY = Math.max(20.0, height * 0.12);
        double chartHeight = bottomY - chartTopY;
        double rightPadding = leftPadding;
        double xGap = values.size() == 1 ? 0 : (double) (width - leftPadding - rightPadding) / (values.size() - 1);

        List<Map<String, Object>> plotPoints = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double x = leftPadding + (i * xGap);
            double normalized = (Math.max(min, Math.min(max, values.get(i))) - min) / range;
            double y = bottomY - (normalized * chartHeight);

            Map<String, Object> point = new HashMap<>();
            point.put("semester", i + 1);
            point.put("value", values.get(i));
            point.put("x", x);
            point.put("y", y);
            plotPoints.add(point);
        }
        chart.put("plotPoints", plotPoints);

        int tickCount = (int) (CHART_MAX_SGPA - CHART_MIN_SGPA) + 1;
        List<Map<String, Object>> yAxisTicks = new ArrayList<>();
        for (int i = 0; i < tickCount; i++) {
            double value = max - i;
            double ratio = (max - value) / range;
            double y = chartTopY + (ratio * chartHeight);
            Map<String, Object> tick = new HashMap<>();
            tick.put("label", String.format(Locale.US, "%.0f", value));
            tick.put("y", y);
            yAxisTicks.add(tick);
        }
        chart.put("yAxisTicks", yAxisTicks);
        return chart;
    }

    private String getStudentDecisionMessage(String usn) {
        return getStudentRequests(usn).stream()
                .filter(req -> req.getStatus() != LeaveStatus.PENDING)
                .findFirst()
                .map(req -> "Leave request #" + req.getId() + " was " + req.getStatus().name().toLowerCase() + ".")
                .orElse("No leave decision yet.");
    }
}
