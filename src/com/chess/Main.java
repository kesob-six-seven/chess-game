package com.chess;

import com.chess.ui.ChessFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the 1v1 Local Chess application.
 */
public class Main {
    public static void main(String[] args) {
        // High-DPI screen scaling optimization
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        // Set modern platform look-and-feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default
        }

        SwingUtilities.invokeLater(() -> {
            ChessFrame frame = new ChessFrame();
            frame.setVisible(true);
        });
    }
}
