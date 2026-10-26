# CodeAlpha_StudentGradeTracker

An enterprise-grade **Student Grade Tracker** desktop application built in Java as part of the **CodeAlpha Java Programming Internship**.

---

## 🚀 Features & Functionality
* **Dynamic Student Registration:** Automatically generates sequential Student IDs (`STD-101`, `STD-102`, etc.) with validation checks[cite: 1].
* **Grade Management:** Dynamically stores multiple subject scores using Java `ArrayList` collections[cite: 1].
* **Statistical Analysis:** Automatically computes individual student averages, highest scores, lowest scores, and letter grades, alongside class-wide KPI metrics[cite: 1].
* **Interactive Java Swing GUI:** A modern, clean desktop dashboard featuring real-time data tables and modal summary reports[cite: 1].
* **File Persistence:** Automatically saves and loads all records via CSV file handling (`data/students_grades.csv`)[cite: 1].

---

## 🛠️ Project Structure
```text
CodeAlpha_StudentGradeTracker/
├── src/
│   └── com/
│       └── codealpha/
│           └── gradetracker/
│               ├── Main.java
│               ├── model/
│               │   └── Student.java
│               ├── service/
│               │   └── GradeTrackerService.java
│               └── ui/
│                   └── GradeTrackerGUI.java
├── data/
│   └── students_grades.csv
└── README.md