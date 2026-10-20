package com.markly.config;

import com.markly.model.*;
import com.markly.repository.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the admin login and some sample data on first start so the app never looks empty. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final StudentRepository students;
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, StudentRepository students, SubjectRepository subjects,
                      AttendanceRepository attendance, PasswordEncoder encoder) {
        this.users = users;
        this.students = students;
        this.subjects = subjects;
        this.attendance = attendance;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            users.save(new AppUser("admin", encoder.encode("admin123"), "Administrator"));
        }
        if (students.count() > 0 || subjects.count() > 0) return;

        String[] names = {"Aarav Sharma", "Diya Patel", "Rohan Mehta", "Isha Nair",
                          "Kabir Singh", "Ananya Iyer", "Vivaan Joshi", "Meera Kulkarni"};
        double[] habits = {0.95, 0.90, 0.70, 0.85, 0.60, 0.92, 0.80, 0.65};
        List<Student> savedStudents = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            String first = names[i].split(" ")[0].toLowerCase();
            savedStudents.add(students.save(new Student(
                    String.format("TE%02d", i + 1), names[i], first + "@example.com", "TE-IT")));
        }
        List<Subject> savedSubjects = subjects.saveAll(List.of(
                new Subject("FSJ", "Full Stack Java Programming"),
                new Subject("DBMS", "Database Management Systems"),
                new Subject("CN", "Computer Networks"),
                new Subject("SE", "Software Engineering")));

        Random random = new Random(42);
        List<AttendanceRecord> records = new ArrayList<>();
        LocalDate day = LocalDate.now();
        int daysAdded = 0;
        while (daysAdded < 10) {
            day = day.minusDays(1);
            if (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
            daysAdded++;
            for (Subject subject : savedSubjects) {
                for (int i = 0; i < savedStudents.size(); i++) {
                    AttendanceRecord r = new AttendanceRecord();
                    r.student = savedStudents.get(i);
                    r.subject = subject;
                    r.attendanceDate = day;
                    r.present = random.nextDouble() < habits[i];
                    records.add(r);
                }
            }
        }
        attendance.saveAll(records);
    }
}
