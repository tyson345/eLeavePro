# eLeavePro (HTML + CSS + Java)

Dark-themed leave management system for the Department of Computer Science and Design.

## Features
- Student and HOD login using USN and password
- Student sign up
- Student dashboard with:
  - leave balance
  - CGPA
  - semester progress
  - leave apply form (reason + from/to dates)
  - leave request status history
- HOD dashboard with:
  - accept/reject leave requests
  - update student records (name, semester, CGPA, leave balance)
  - department progress report (students count, average CGPA, leave stats)

## Tech
- Java 17
- Spring Boot
- Thymeleaf (HTML templates)
- CSS (dark theme)
- In-memory data store (no DB required for demo)

## Run
1. Install Java 17+
2. Install Maven
3. From project root:
   - `mvn spring-boot:run`
4. Open:
   - `http://localhost:8080/login`

## Demo Credentials
- HOD:
  - USN: `CSDHOD001`
  - Password: `hod123`
- Student:
  - USN: `4PM22CG001`
  - Password: `student123`

## Deploy (Render - Recommended)
This app is a Spring Boot server-side app (not a static site), so deploy it to a Java/Docker host like Render.

### Option A: Blueprint deploy (uses `render.yaml`)
1. Push this repo to GitHub.
2. In Render, choose **New +** -> **Blueprint**.
3. Select this repository.
4. Render reads `render.yaml` and deploys automatically.

### Option B: Web Service from Dockerfile
1. In Render, choose **New +** -> **Web Service**.
2. Connect your GitHub repo.
3. Environment: **Docker**.
4. Deploy.

Notes:
- `PORT` is read from environment with fallback to `8080`.
- Browser auto-open is disabled in deploy environments using `AUTO_OPEN_BROWSER=false`.
