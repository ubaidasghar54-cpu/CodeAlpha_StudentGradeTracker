package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeTrackerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modern Java Swing GUI for the CodeAlpha Student Grade Tracker.
 */
public class GradeTrackerGUI extends JFrame {
    private final GradeTrackerService service;

    private JLabel totalStudentsLabel;
    private JLabel classAvgLabel;
    private JLabel classHighestLabel;
    private JLabel classLowestLabel;

    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField initialGradeField;

    private JComboBox<String> studentSelectorCombo;
    private JTextField additionalGradeField;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    public GradeTrackerGUI(GradeTrackerService service) {
        this.service = service;
        initializeUI();
        refreshDashboard();
    }

    private void initializeUI() {
        setTitle("CodeAlpha - Student Grade Tracker System");
        setSize(1020, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JPanel mainContainer = new JPanel(new BorderLayout(12, 12));
        mainContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContainer.setBackground(new Color(245, 247, 250));

        // 1. Top Header & KPI Statistics Cards
        mainContainer.add(createTopPanel(), BorderLayout.NORTH);

        // 2. Center Split: Left Controls + Right Student Table
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(createControlPanel(), BorderLayout.WEST);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Action Bar
        mainContainer.add(createBottomPanel(), BorderLayout.SOUTH);

        add(mainContainer);
    }

    private JPanel createTopPanel() {
        JPanel topWrapper = new JPanel(new BorderLayout(10, 10));
        topWrapper.setOpaque(false);

        JLabel title = new JLabel("Student Grade Tracker Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 41, 59));
        topWrapper.add(title, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        statsGrid.setOpaque(false);

        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);
        classAvgLabel = new JLabel("0.00%", SwingConstants.CENTER);
        classHighestLabel = new JLabel("0.00%", SwingConstants.CENTER);
        classLowestLabel = new JLabel("0.00%", SwingConstants.CENTER);

        statsGrid.add(createStatCard("Total Students", totalStudentsLabel, new Color(37, 99, 235)));
        statsGrid.add(createStatCard("Class Average", classAvgLabel, new Color(16, 185, 129)));
        statsGrid.add(createStatCard("Highest Score", classHighestLabel, new Color(139, 92, 246)));
        statsGrid.add(createStatCard("Lowest Score", classLowestLabel, new Color(245, 158, 11)));

        topWrapper.add(statsGrid, BorderLayout.CENTER);
        return topWrapper;
    }

    private JPanel createStatCard(String titleText, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createControlPanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(310, 0));
        sidePanel.setOpaque(false);

        // Card 1: Add New Student
        JPanel addStudentPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        addStudentPanel.setBackground(Color.WHITE);
        addStudentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " 1. Register New Student ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
                ),
                new EmptyBorder(10, 12, 12, 12)
        ));

        studentIdField = new JTextField();
        studentNameField = new JTextField();
        initialGradeField = new JTextField();

        addStudentPanel.add(new JLabel("Student ID (e.g., STD-104):"));
        addStudentPanel.add(studentIdField);
        addStudentPanel.add(new JLabel("Full Name:"));
        addStudentPanel.add(studentNameField);
        addStudentPanel.add(new JLabel("Initial Score (0 - 100, Optional):"));
        addStudentPanel.add(initialGradeField);

        JButton addStudentBtn = createStyledButton("Add Student", new Color(37, 99, 235));
        addStudentBtn.addActionListener(e -> handleAddStudent());
        addStudentPanel.add(Box.createVerticalStrut(4));
        addStudentPanel.add(addStudentBtn);

        // Card 2: Add Grade to Existing Student
        JPanel addGradePanel = new JPanel(new GridLayout(0, 1, 6, 6));
        addGradePanel.setBackground(Color.WHITE);
        addGradePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " 2. Add Subject Score ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
                ),
                new EmptyBorder(10, 12, 12, 12)
        ));

        studentSelectorCombo = new JComboBox<>();
        additionalGradeField = new JTextField();

        addGradePanel.add(new JLabel("Select Student:"));
        addGradePanel.add(studentSelectorCombo);
        addGradePanel.add(new JLabel("Enter Score (0 - 100):"));
        addGradePanel.add(additionalGradeField);

        JButton addGradeBtn = createStyledButton("Record Score", new Color(16, 185, 129));
        addGradeBtn.addActionListener(e -> handleAddGrade());
        addGradePanel.add(Box.createVerticalStrut(4));
        addGradePanel.add(addGradeBtn);

        sidePanel.add(addStudentPanel);
        sidePanel.add(Box.createVerticalStrut(12));
        sidePanel.add(addGradePanel);
        return sidePanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Student Grade Records ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
                ),
                new EmptyBorder(8, 8, 8, 8)
        ));

        String[] columns = {"ID", "Student Name", "Recorded Scores", "Average", "Highest", "Lowest", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(241, 245, 249));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        return tableContainer;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton deleteBtn = createStyledButton("Delete Selected Student", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> handleDeleteSelected());

        JButton reportBtn = createStyledButton("View Summary Report", new Color(139, 92, 246));
        reportBtn.addActionListener(e -> showSummaryReportDialog());

        bottomPanel.add(deleteBtn);
        bottomPanel.add(reportBtn);
        return bottomPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleAddStudent() {
        try {
            String id = studentIdField.getText().trim();
            String name = studentNameField.getText().trim();
            String gradeStr = initialGradeField.getText().trim();

            Student created = service.addStudent(id, name);
            if (!gradeStr.isEmpty()) {
                double initialScore = Double.parseDouble(gradeStr);
                service.addGradeToStudent(created.getStudentId(), initialScore);
            }

            studentIdField.setText("");
            studentNameField.setText("");
            initialGradeField.setText("");
            refreshDashboard();
            JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Initial score must be a valid number between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddGrade() {
        if (studentSelectorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String selectedItem = studentSelectorCombo.getSelectedItem().toString();
            String studentId = selectedItem.split(" - ")[0].trim();
            double score = Double.parseDouble(additionalGradeField.getText().trim());

            service.addGradeToStudent(studentId, score);
            additionalGradeField.setText("");
            refreshDashboard();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric score (0 - 100).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSelected() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student row in the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String studentId = tableModel.getValueAt(selectedRow, 0).toString();
        String studentName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + studentName + " (" + studentId + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            service.removeStudent(studentId);
            refreshDashboard();
        }
    }

    private void showSummaryReportDialog() {
        JTextArea reportArea = new JTextArea(service.generateSummaryReport());
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(720, 420));

        JOptionPane.showMessageDialog(this, scrollPane, "Summary Report (Console & CSV View)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshDashboard() {
        List<Student> allStudents = service.getAllStudents();

        // Update KPI cards
        totalStudentsLabel.setText(String.valueOf(allStudents.size()));
        classAvgLabel.setText(String.format("%.2f%%", service.getClassAverage()));
        classHighestLabel.setText(String.format("%.2f%%", service.getClassHighestScore()));
        classLowestLabel.setText(String.format("%.2f%%", service.getClassLowestScore()));

        // Update Table and Dropdown
        tableModel.setRowCount(0);
        studentSelectorCombo.removeAllItems();

        for (Student s : allStudents) {
            String gradesList = s.getGrades().isEmpty()
                    ? "No scores yet"
                    : s.getGrades().stream()
                            .map(g -> String.format("%.1f", g))
                            .collect(Collectors.joining(", "));

            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    gradesList,
                    String.format("%.2f%%", s.getAverageScore()),
                    String.format("%.2f%%", s.getHighestScore()),
                    String.format("%.2f%%", s.getLowestScore()),
                    s.getLetterGrade()
            });

            studentSelectorCombo.addItem(s.getStudentId() + " - " + s.getName());
        }
    }
}