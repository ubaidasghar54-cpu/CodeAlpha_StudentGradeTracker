package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.Student;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class managing the ArrayList of students, automatic Student ID generation,
 * class-wide statistical calculations, summary report generation, and CSV file persistence.
 */
public class GradeTrackerService {
    private final List<Student> students;
    private static final String DATA_FILE = "data/students_grades.csv";

    public GradeTrackerService() {
        this.students = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Automatically generates the next sequential Student ID (e.g., STD-101, STD-102, ...).
     */
    public String generateNextStudentId() {
        int maxNumber = 100;
        for (Student s : students) {
            String id = s.getStudentId().toUpperCase();
            if (id.startsWith("STD-")) {
                try {
                    int num = Integer.parseInt(id.substring(4).trim());
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return "STD-" + (maxNumber + 1);
    }

    /**
     * Adds a student with an automatically generated Student ID.
     */
    public Student addStudent(String name) {
        return addStudent(generateNextStudentId(), name);
    }

    public Student addStudent(String studentId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Name cannot be empty.");
        }
        String finalId = (studentId == null || studentId.trim().isEmpty())
                ? generateNextStudentId()
                : studentId.trim();

        if (findStudentById(finalId) != null) {
            throw new IllegalArgumentException("A student with ID '" + finalId + "' already exists.");
        }

        Student student = new Student(finalId, name.trim());
        students.add(student);
        saveToFile();
        return student;
    }

    public void addGradeToStudent(String studentId, double grade) {
        Student student = findStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID '" + studentId + "' not found.");
        }
        student.addGrade(grade);
        saveToFile();
    }

    public boolean removeStudent(String studentId) {
        Student student = findStudentById(studentId);
        if (student != null) {
            students.remove(student);
            saveToFile();
            return true;
        }
        return false;
    }

    public Student findStudentById(String studentId) {
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public double getClassAverage() {
        double sum = 0;
        int totalGradesCount = 0;
        for (Student s : students) {
            for (double g : s.getGrades()) {
                sum += g;
                totalGradesCount++;
            }
        }
        return totalGradesCount == 0 ? 0.0 : sum / totalGradesCount;
    }

    public double getClassHighestScore() {
        double highest = 0.0;
        boolean found = false;
        for (Student s : students) {
            for (double g : s.getGrades()) {
                if (!found || g > highest) {
                    highest = g;
                    found = true;
                }
            }
        }
        return found ? highest : 0.0;
    }

    public double getClassLowestScore() {
        double lowest = 0.0;
        boolean found = false;
        for (Student s : students) {
            for (double g : s.getGrades()) {
                if (!found || g < lowest) {
                    lowest = g;
                    found = true;
                }
            }
        }
        return found ? lowest : 0.0;
    }

    public Student getTopPerformingStudent() {
        Student top = null;
        double maxAvg = -1.0;
        for (Student s : students) {
            if (!s.getGrades().isEmpty() && s.getAverageScore() > maxAvg) {
                maxAvg = s.getAverageScore();
                top = s;
            }
        }
        return top;
    }

    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("                     CODEALPHA - STUDENT GRADE SUMMARY REPORT                   \n");
        sb.append("================================================================================\n");
        sb.append(String.format("Total Students Enrolled : %d\n", students.size()));
        sb.append(String.format("Overall Class Average   : %.2f%%\n", getClassAverage()));
        sb.append(String.format("Highest Score Recorded  : %.2f%%\n", getClassHighestScore()));
        sb.append(String.format("Lowest Score Recorded   : %.2f%%\n", getClassLowestScore()));
        Student top = getTopPerformingStudent();
        if (top != null) {
            sb.append(String.format("Top Performing Student  : %s (%s) - Avg: %.2f%% [%s]\n",
                    top.getName(), top.getStudentId(), top.getAverageScore(), top.getLetterGrade()));
        }
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("%-12s | %-22s | %-8s | %-8s | %-8s | %-5s\n",
                "ID", "Student Name", "Average", "Highest", "Lowest", "Grade"));
        sb.append("--------------------------------------------------------------------------------\n");

        if (students.isEmpty()) {
            sb.append("No student records available.\n");
        } else {
            for (Student s : students) {
                sb.append(String.format("%-12s | %-22s | %7.2f%% | %7.2f%% | %7.2f%% | %-5s\n",
                        s.getStudentId(),
                        s.getName(),
                        s.getAverageScore(),
                        s.getHighestScore(),
                        s.getLowestScore(),
                        s.getLetterGrade()));
            }
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    public void saveToFile() {
        File file = new File(DATA_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Student s : students) {
                StringBuilder line = new StringBuilder();
                line.append(s.getStudentId()).append(",").append(s.getName());
                for (double g : s.getGrades()) {
                    line.append(",").append(g);
                }
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save student data: " + e.getMessage());
        }
    }

    public final void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedSampleData();
            return;
        }
        students.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Student s = new Student(parts[0].trim(), parts[1].trim());
                    for (int i = 2; i < parts.length; i++) {
                        try {
                            s.addGrade(Double.parseDouble(parts[i].trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    students.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load student data: " + e.getMessage());
        }
    }

    private void seedSampleData() {
        Student s1 = new Student("STD-101", "Ubaid Ullah");
        s1.addGrade(94.0);
        s1.addGrade(89.5);
        s1.addGrade(96.0);

        Student s2 = new Student("STD-102", "Ayesha Khan");
        s2.addGrade(82.0);
        s2.addGrade(78.5);
        s2.addGrade(88.0);

        Student s3 = new Student("STD-103", "Ali Raza");
        s3.addGrade(65.0);
        s3.addGrade(71.0);
        s3.addGrade(68.5);

        students.add(s1);
        students.add(s2);
        students.add(s3);
        saveToFile();
    }
}