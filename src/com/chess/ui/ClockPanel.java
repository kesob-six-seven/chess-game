package com.chess.ui;

import com.chess.model.PieceColor;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * Chess clock supporting untimed mode, preset time controls, increments,
 * and visual countdowns with low-time indicators.
 */
public class ClockPanel extends JPanel {
    public interface TimeoutListener {
        void onTimeout(PieceColor timedOutColor);
    }

    private final JLabel whiteTimeLabel;
    private final JLabel blackTimeLabel;
    private final JPanel whiteCard;
    private final JPanel blackCard;

    private int whiteTimeSeconds;
    private int blackTimeSeconds;
    private int incrementSeconds;
    private boolean untimed = true;

    private PieceColor activeTurn = PieceColor.WHITE;
    private boolean running = false;
    private final Timer countdownTimer;
    private TimeoutListener timeoutListener;

    public ClockPanel() {
        setLayout(new GridLayout(1, 2, 12, 0));
        setOpaque(false);

        whiteCard = createCardPanel();
        whiteTimeLabel = createTimeLabel("Untimed");
        whiteCard.add(createLabelTitle("WHITE"));
        whiteCard.add(whiteTimeLabel);

        blackCard = createCardPanel();
        blackTimeLabel = createTimeLabel("Untimed");
        blackCard.add(createLabelTitle("BLACK"));
        blackCard.add(blackTimeLabel);

        add(whiteCard);
        add(blackCard);

        countdownTimer = new Timer(1000, e -> tick());
        updateCardStyles();
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2));
        card.setBackground(new Color(45, 47, 52));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 68, 75), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return card;
    }

    private JLabel createLabelTitle(String title) {
        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(170, 175, 185));
        return lbl;
    }

    private JLabel createTimeLabel(String initialText) {
        JLabel lbl = new JLabel(initialText, JLabel.CENTER);
        lbl.setFont(new Font("Consolas", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    public void setTimeControls(int baseMinutes, int incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
        this.untimed = (baseMinutes <= 0);

        if (untimed) {
            whiteTimeSeconds = 0;
            blackTimeSeconds = 0;
            whiteTimeLabel.setText("Untimed");
            blackTimeLabel.setText("Untimed");
            countdownTimer.stop();
            running = false;
        } else {
            whiteTimeSeconds = baseMinutes * 60;
            blackTimeSeconds = baseMinutes * 60;
            updateLabels();
        }
        updateCardStyles();
    }

    public void setTimeoutListener(TimeoutListener listener) {
        this.timeoutListener = listener;
    }

    public void setActiveTurn(PieceColor color) {
        if (!untimed && running && activeTurn != null) {
            // Add increment to the player who just finished their turn
            if (activeTurn == PieceColor.WHITE) {
                whiteTimeSeconds += incrementSeconds;
            } else {
                blackTimeSeconds += incrementSeconds;
            }
            updateLabels();
        }
        this.activeTurn = color;
        updateCardStyles();
    }

    public void start() {
        if (!untimed) {
            running = true;
            countdownTimer.start();
            updateCardStyles();
        }
    }

    public void stop() {
        running = false;
        countdownTimer.stop();
        updateCardStyles();
    }

    public void reset() {
        stop();
        if (!untimed) {
            updateLabels();
        }
        updateCardStyles();
    }

    private void tick() {
        if (!running || untimed) return;

        if (activeTurn == PieceColor.WHITE) {
            if (whiteTimeSeconds > 0) {
                whiteTimeSeconds--;
                if (whiteTimeSeconds == 0) {
                    stop();
                    if (timeoutListener != null) {
                        timeoutListener.onTimeout(PieceColor.WHITE);
                    }
                }
            }
        } else {
            if (blackTimeSeconds > 0) {
                blackTimeSeconds--;
                if (blackTimeSeconds == 0) {
                    stop();
                    if (timeoutListener != null) {
                        timeoutListener.onTimeout(PieceColor.BLACK);
                    }
                }
            }
        }
        updateLabels();
        updateCardStyles();
    }

    private void updateLabels() {
        if (untimed) {
            whiteTimeLabel.setText("Untimed");
            blackTimeLabel.setText("Untimed");
        } else {
            whiteTimeLabel.setText(formatTime(whiteTimeSeconds));
            blackTimeLabel.setText(formatTime(blackTimeSeconds));
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateCardStyles() {
        boolean whiteActive = running && activeTurn == PieceColor.WHITE;
        boolean blackActive = running && activeTurn == PieceColor.BLACK;

        // White card styling
        if (whiteActive) {
            whiteCard.setBackground(new Color(58, 65, 78));
            whiteCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(110, 170, 250), 2, true),
                    BorderFactory.createEmptyBorder(5, 11, 5, 11)
            ));
        } else {
            whiteCard.setBackground(new Color(36, 38, 42));
            whiteCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 58, 65), 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }

        // Black card styling
        if (blackActive) {
            blackCard.setBackground(new Color(58, 65, 78));
            blackCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(110, 170, 250), 2, true),
                    BorderFactory.createEmptyBorder(5, 11, 5, 11)
            ));
        } else {
            blackCard.setBackground(new Color(36, 38, 42));
            blackCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 58, 65), 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }

        // Low time alert colors (< 30s)
        if (!untimed) {
            whiteTimeLabel.setForeground(whiteTimeSeconds <= 30 ? new Color(245, 90, 80) : Color.WHITE);
            blackTimeLabel.setForeground(blackTimeSeconds <= 30 ? new Color(245, 90, 80) : Color.WHITE);
        } else {
            whiteTimeLabel.setForeground(new Color(200, 205, 215));
            blackTimeLabel.setForeground(new Color(200, 205, 215));
        }
    }
}
