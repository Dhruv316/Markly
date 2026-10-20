# Markly - Attendance made simple

A clean, light-themed attendance management system.
Spring Boot REST API + React front end + MySQL, all running in Docker.

## Run it (one command)

Install Docker Desktop, then from this folder:

    docker compose up --build

Wait until the logs show the backend has started (the first build takes a few minutes),
then open **http://localhost:3000**

Login: `admin` / `admin123`  (sample students, subjects and attendance are added on first start)

Stop with `Ctrl+C`. To also wipe the database: `docker compose down -v`

## Features
- Login (BCrypt-hashed password, token-protected API)
- Students: add, edit, delete, search
- Subjects: add, delete
- Mark attendance by subject and date (everyone starts present, untick absentees, edit later)
- Reports per student, per subject or overall, with a 75% rule and CSV download
- Dashboard with totals, average attendance and a low-attendance list

## Project structure
    docker-compose.yml     db (MySQL) + backend + frontend
    backend/               Spring Boot 3 / Java 17
      model/               JPA entities (Student, Subject, AttendanceRecord, AppUser)
      repository/          Spring Data JPA repositories
      controller/          REST controllers (auth, students, subjects, attendance, reports)
      exception/           custom ApiException + global exception handler
      config/              auth interceptor, token store, sample-data seeder
    frontend/              React 18 + Vite, served by nginx (which also proxies /api to the backend)

## REST API (all except login need `Authorization: Bearer <token>`)
    POST   /api/auth/login
    GET/POST            /api/students       PUT/DELETE /api/students/{id}
    GET/POST            /api/subjects       DELETE     /api/subjects/{id}
    GET    /api/attendance?subjectId=&date=     POST /api/attendance
    GET    /api/reports/summary?subjectId=      GET  /api/dashboard

## Syllabus topics used
OOP and inheritance-style layering, custom exceptions, Spring Boot REST APIs, database integration (JPA/Hibernate on MySQL),
MVC-style separation, React components, state, props, hooks and routing, plus DOM events through React.

## Run without Docker (optional)
Backend: install Java 17, Maven and MySQL, then `cd backend && mvn spring-boot:run`
(set `DB_URL`, `DB_USER`, `DB_PASSWORD` if your MySQL differs).
Frontend: `cd frontend && npm install && npm run dev` then open http://localhost:5173

Created by DHRUV RANE, Bharat College of Engineering
