# LeaveApp Project Notes (Presentation Guide)

This file explains how the project works end-to-end, where to change code, and common questions you may be asked in presentation/viva.

---

## 1) Project Overview

- Project name: `leaveapp`
- Stack:
  - Java 17
  - Spring Boot 3 (`spring-boot-starter-web`)
  - Thymeleaf templates (`spring-boot-starter-thymeleaf`)
  - CSS + small vanilla JavaScript
- Type: Server-rendered web app (no React/Angular).
- Data storage: **in-memory collections** inside service class (no external DB currently).

Main use-cases:
- Student login + signup (activation based on pre-seeded USN)
- Student leave apply + leave status tracking
- HOD leave approval/rejection
- HOD student profile/marks update
- HOD department analytics and student progress

---

## 2) Folder Structure (Important)

- `src/main/java/com/csd/leaveapp/LeaveAppApplication.java`
  - Spring Boot entry point.
- `src/main/java/com/csd/leaveapp/controller/`
  - `AuthController.java` (login/signup/logout)
  - `StudentController.java` (student dashboard/leave/marks)
  - `HodController.java` (HOD pages + update actions)
- `src/main/java/com/csd/leaveapp/service/AppService.java`
  - Core business logic + in-memory data store + reporting.
- `src/main/java/com/csd/leaveapp/model/`
  - Domain classes (`User`, `SemesterRecord`, `LeaveRequest`, enums).
- `src/main/resources/templates/`
  - Thymeleaf HTML pages.
- `src/main/resources/static/css/style.css`
  - Global UI and responsiveness.
- `src/main/resources/static/js/trend-chart.js`
  - Chart hover interactions.
- `pom.xml`
  - Maven build and dependencies.
- `src/main/resources/application.properties`
  - runtime configs (port, thymeleaf cache).

---

## 3) How Request Flow Works

Typical flow:
1. Browser calls URL (example `/hod/students`).
2. Spring controller method runs.
3. Controller calls `AppService` for data.
4. Controller attaches data in `Model`.
5. Thymeleaf template renders HTML with that data.
6. Browser displays page and optional JS enhances UX.

Example:
- URL: `/hod/reports`
- Controller: `HodController.reportsPage(...)`
- Service calls: `getSemesterRecords(8)`, `getDepartmentTrendReport()`
- Template: `hod-reports.html`

---

## 4) Authentication + Session

Implemented in `AuthController.java`.

- Login (`POST /login`) checks:
  - `usn`
  - `password`
  - `role`
- On success, session stores:
  - `session["usn"]`
  - `session["role"]`
- Access checks:
  - Student pages validate `role == STUDENT`
  - HOD pages validate `role == HOD`
- Logout (`/logout`) invalidates session.

No JWT/security framework currently; this is session-based app-level auth.

---

## 5) Data Model Details

### `User`
- `usn`, `name`, `password`, `role`
- `cgpa`, `semester`, `leaveBalance`
- `active` (for signup activation)
- `semesterRecords` list

### `SemesterRecord` (current)
- `semester`
- `sgpa`
- `totalMarks`
- `acquiredMarks`

### `LeaveRequest`
- leave id, student info, date range, reason, number of days, status.

Enums:
- `Role`: `HOD`, `STUDENT`
- `LeaveStatus`: `PENDING`, `APPROVED`, `REJECTED`

---

## 6) Marks Logic (Current)

Internal/external were removed. Current marks model:
- `totalMarks`
- `acquiredMarks`
- `sgpa`
- `cgpa`

Rules:
- SGPA is computed as:
  - `sgpa = (acquiredMarks / totalMarks) * 10`
  - rounded to 2 decimals.
- CGPA is average of all semester SGPAs with value > 0.

Where:
- Validation + update in `AppService.updateStudentMarks(...)`.

---

## 7) CGPA Precision (2 Digits)

All display targets were aligned to show 2 decimals where relevant.

Example formatting used in templates:
- `#numbers.formatDecimal(value, 1, 2)`

This prevents long floating values like `7.4662500000000001`.

---

## 8) Department Reports Page (Dynamic UI)

Current reports UI includes:
- Top toolbar with:
  - section switch buttons
  - semester load control
  - student quick search + quick open progress
- Switchable report sections:
  - Semester Students
  - Department Trend
  - Semester Summary
- Sticky table headers in scrollable containers.

Files:
- `templates/hod-reports.html`
- `static/css/style.css`

---

## 9) Global UI/Responsive Design

All pages share `style.css`.

Highlights:
- Theme variables in `:root` (colors/shadows)
- Premium hover/focus animations for:
  - cards
  - buttons
  - tabs
  - table rows
  - chips
- Responsive behavior via media queries (`@media (max-width: 860px)`):
  - grid stacks into single-column
  - toolbar/forms adapt to vertical layout
  - sticky elements disable on small screens where needed

To change site-wide look:
- Edit color tokens in `:root`
- Adjust card/button/tab styles once in `style.css`

---

## 10) Footer/Copyright

Added to all pages:
- `© Sourabh patil Computer science and design dept 2022-2026`

Style class:
- `.page-footer` in `style.css`

---

## 11) How Maven Works Here

File: `pom.xml`

Key points:
- Parent: `spring-boot-starter-parent` (`3.3.4`)
- Java version: `17`
- Dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-thymeleaf`
- Plugin:
  - `spring-boot-maven-plugin`

Common commands:
- Run app:
  - `mvn spring-boot:run`
- Compile only:
  - `mvn -DskipTests compile`
- Package jar:
  - `mvn clean package`

---

## 12) Database Connection Status (Very Important)

Current project uses **no real database**.

Instead:
- Data is seeded in-memory in `AppService.seedData()`.
- Collections used:
  - `Map<String, User> users`
  - `Map<Long, LeaveRequest> leaveRequests`

Effect:
- On restart, data resets to default seed state.
- Good for demo/prototype, not production persistence.

### If you want real DB later
Typical migration:
1. Add dependencies:
   - `spring-boot-starter-data-jpa`
   - DB driver (MySQL/PostgreSQL)
2. Add DB config in `application.properties`
3. Convert model classes to JPA entities (`@Entity`)
4. Create repository interfaces (`JpaRepository`)
5. Replace in-memory map logic in `AppService` with repository calls
6. Add schema migration tool (Flyway/Liquibase optional but recommended)

---

## 13) How to Add New Student from Code

Current method:
- Open `AppService.java`
- In `seedData()`, add:
  - `addStudent("USN", "NAME");`

Example:
- `addStudent("4PM22CG062", "NEW STUDENT");`

What happens:
- Student is preloaded in memory with generated semester records.
- Student can sign up using that USN.

---

## 14) How to Change UI in Future

### A) Global changes (all pages)
- File: `static/css/style.css`
- Best for colors, shadows, typography, spacing, hover effects.

### B) One page only
- Edit matching template in `templates/`
  - Example: `hod-students.html` for students management page.

### C) Dynamic behavior
- Small page-specific JS exists inside templates.
- Shared chart behavior in:
  - `static/js/trend-chart.js`

### D) Data shown on page
- Update controller method and/or service method feeding that template.

---

## 15) Important Backend Methods (Quick Reference)

In `AppService.java`:
- `login(...)`
- `registerStudent(...)`
- `applyLeave(...)`
- `decideLeave(...)`
- `updateStudent(...)`
- `updateStudentMarks(...)`
- `departmentReport()`
- `getSemesterRecords(int semester)`
- `getDepartmentTrendReport()`
- `getStudentTrendReport(String usn)`

In controllers:
- `AuthController` for auth routes
- `StudentController` for student views/actions
- `HodController` for HOD views/actions

---

## 16) How Responsive UI Is Managed

Implemented with CSS media query:
- `@media (max-width: 860px)`

At this breakpoint:
- multi-column grids become single-column
- inline forms stack vertically
- report toolbar adapts for mobile
- sticky side panels become normal flow

So same pages work for desktop + tablet/mobile without separate templates.

---

## 17) Configuration Notes

`application.properties`:
- `server.port=8080` -> app runs on port 8080
- `spring.thymeleaf.cache=false` -> template changes visible without hard caching issues in dev

---

## 18) Known Limitations (You can mention in presentation)

- In-memory data only (restart resets data).
- No advanced auth/security framework.
- No API layer separation; server-rendered app.
- Basic validation (good for demo, can be stronger).
- No automated tests currently.

---

## 19) Suggested Future Improvements

- Add real DB (MySQL/Postgres) and JPA repositories.
- Add audit logs (who changed marks/leave decisions).
- Add Spring Security with role-based route protection.
- Add unit/integration tests.
- Add export (CSV/PDF) for reports.
- Add subject-wise marks module if needed later.

---

## 20) Presentation / Viva Questions You May Be Asked

### Architecture
1. Why did you choose Spring Boot + Thymeleaf?
2. How is MVC implemented in this project?
3. Where is business logic kept and why?
4. How are pages connected to controller methods?

### Authentication
5. How do you manage session after login?
6. How do you restrict student vs HOD pages?
7. What are the security limitations of current approach?

### Data & Marks
8. How is SGPA calculated?
9. How is CGPA calculated?
10. Why did you move from internal/external to total/acquired marks?
11. How do you validate invalid marks input?

### Reports
12. How is department trend generated?
13. How is individual student progress generated?
14. How did you improve reports usability and reduce scrolling?

### UI/UX
15. How is responsiveness handled?
16. Where are global styles defined?
17. How did you implement premium hover effects?
18. How can you switch to light theme quickly?

### Build/Deployment
19. What is Maven’s role in this project?
20. What does `spring-boot-maven-plugin` do?
21. How do you run/compile/package this app?

### Persistence
22. Is there a database now?
23. What changes are needed to connect MySQL/Postgres?
24. What will happen to data on restart currently?

### Extensibility
25. How to add a new student?
26. How to add a new report page?
27. How to add new role (e.g., faculty)?
28. Where to change form fields or validation?

---

## 21) Short Demo Script (Optional)

1. Login as HOD.
2. Open Students & Marks.
3. Search student by USN.
4. Update one semester using total/acquired marks.
5. Show SGPA auto-calculation + updated CGPA.
6. Open Department Reports, switch sections from toolbar.
7. Quick open Individual Progress from top search.
8. Show responsive behavior by resizing browser.

---

## 22) One-Line Summary for Panel

"This is a Spring Boot + Thymeleaf leave and academic tracking app with role-based flows, in-memory domain modeling, dynamic HOD reporting UI, and centralized responsive styling for fast iteration and demo-ready usability."

