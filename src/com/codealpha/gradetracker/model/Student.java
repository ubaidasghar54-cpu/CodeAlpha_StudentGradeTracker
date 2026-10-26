package com.codealpha.gradetracker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model class representing a Student and their grades.
 * Demonstrates encapsulation and ArrayList management.
 */
public class Student {
    private String studentId;
    private String name;
    private List<Double> grades;

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public void addGrade(double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
        this.grades.add(grade);
    }

    public double getAverageScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (double g : grades) {
            sum += g;
        }
        return sum / grades.size();
    }

    public double getHighestScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        return Collections.max(grades);
    }

    public double getLowestScore() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        return Collections.min(grades);
    }

    public String getLetterGrade() {
        double avg = getAverageScore();
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 50) return "D";
        return "F";
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public List<Double> getGrades() {
        return new ArrayList<>(grades);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Avg: %.2f [%s]", name, studentId, getAverageScore(), getLetterGrade());
    }
}