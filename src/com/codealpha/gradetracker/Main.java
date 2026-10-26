package com.codealpha.gradetracker;

import com.codealpha.gradetracker.service.GradeTrackerService;
import com.codealpha.gradetracker.ui.GradeTrackerGUI;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the CodeAlpha Student Grade Tracker Application.
 */
public class Main {
    public static void main(String[] args) {
        // Apply system look and feel for clean native styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        GradeTrackerService service = new GradeTrackerService();

        // Print summary report to console on startup as well
        System.out.println(service.generateSummaryReport());

        // Launch the Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GradeTrackerGUI app = new GradeTrackerGUI(service);
            app.setVisible(true);
        });
    }
}