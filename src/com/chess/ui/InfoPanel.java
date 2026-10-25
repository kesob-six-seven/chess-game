package com.chess.ui;

import com.chess.model.GameState;
import com.chess.model.MoveHistory;
import com.chess.model.Piece;
import com.chess.model.PieceColor;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Sidebar panel displaying turn status, captured pieces, clocks,
 * move history log, and game control buttons.
 */
public class InfoPanel extends JPanel {
    public interface GameActionListener {
        void onNewGame();
        void onUndo();
        void onOfferDraw();
        void onResign();
        void onToggleFlip();
        void onThemeChanged(ChessTheme theme);
        void onTimeModeChanged(int minutes, int increment);
    }

    private final JLabel turnStatusLabel;
    private final JLabel gameStateBanner;
    private final CapturedPanel topCapturedPanel;
    private final CapturedPanel bottomCapturedPanel;
    private final ClockPanel clockPanel;
    private final MoveHistoryPanel moveHistoryPanel;

    private final JButton undoBtn;
    private final JButton drawBtn;
    private final JButton resignBtn;
    private final JButton newGameBtn;
    private final JButton flipBtn;

    private final JComboBox<ChessTheme> themeCombo;
    private final JComboBox<String> timeControlCombo;

    private GameActionListener actionListener;

    public InfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 640));
        setBackground(new Color(28, 30, 34));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(45, 48, 55)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // 1. Clock Panel
        clockPanel = new ClockPanel();
        add(clockPanel);
        add(Box.createVerticalStrut(10));

        // 2. Status Banner (Turn / Check / Checkmate)
        JPanel statusBox = new JPanel(new BorderLayout(4, 4));
        statusBox.setBackground(new Color(38, 41, 48));
        statusBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 58, 66), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        turnStatusLabel = new JLabel("White's Turn", JLabel.CENTER);
        turnStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        turnStatusLabel.setForeground(Color.WHITE);

        gameStateBanner = new JLabel("Game in progress", JLabel.CENTER);
        gameStateBanner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gameStateBanner.setForeground(new Color(160, 165, 175));

        statusBox.add(turnStatusLabel, BorderLayout.NORTH);
        statusBox.add(gameStateBanner, BorderLayout.SOUTH);
        add(statusBox);
        add(Box.createVerticalStrut(10));

        // 3. Captured Pieces (Opponent / Black pieces lost)
        JLabel blackLossLabel = new JLabel("Captured Black Pieces:", JLabel.LEFT);
        blackLossLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        blackLossLabel.setForeground(new Color(150, 155, 165));
        add(blackLossLabel);
        topCapturedPanel = new CapturedPanel(PieceColor.WHITE);
        add(topCapturedPanel);
        add(Box.createVerticalStrut(6));

        // 4. Move History Panel
        moveHistoryPanel = new MoveHistoryPanel();
        add(moveHistoryPanel);
        add(Box.createVerticalStrut(6));

        // 5. Captured Pieces (White pieces lost)
        JLabel whiteLossLabel = new JLabel("Captured White Pieces:", JLabel.LEFT);
        whiteLossLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        whiteLossLabel.setForeground(new Color(150, 155, 165));
        add(whiteLossLabel);
        bottomCapturedPanel = new CapturedPanel(PieceColor.BLACK);
        add(bottomCapturedPanel);
        add(Box.createVerticalStrut(10));

        // 6. Settings Controls (Theme & Time Control Dropdowns)
        JPanel dropdownPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        dropdownPanel.setOpaque(false);

        JLabel themeLbl = new JLabel("Board Theme:");
        themeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        themeLbl.setForeground(new Color(180, 185, 195));

        themeCombo = new JComboBox<>(ChessTheme.values());
        themeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeCombo.setBackground(new Color(45, 48, 55));
        themeCombo.setForeground(Color.WHITE);
        themeCombo.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onThemeChanged((ChessTheme) themeCombo.getSelectedItem());
            }
        });

        JLabel timeLbl = new JLabel("Time Control:");
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLbl.setForeground(new Color(180, 185, 195));

        String[] timeOptions = {"Untimed", "3 min (Blitz)", "5 min (Blitz)", "10 min (Rapid)", "15 | +10s", "30 min (Classical)"};
        timeControlCombo = new JComboBox<>(timeOptions);
        timeControlCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeControlCombo.setBackground(new Color(45, 48, 55));
        timeControlCombo.setForeground(Color.WHITE);
        timeControlCombo.addActionListener(e -> handleTimeControlChange());

        dropdownPanel.add(themeLbl);
        dropdownPanel.add(themeCombo);
        dropdownPanel.add(timeLbl);
        dropdownPanel.add(timeControlCombo);
        add(dropdownPanel);
        add(Box.createVerticalStrut(10));

        // 7. Action Buttons (Undo, Flip, Draw, Resign, New Game)
        JPanel actionButtonGrid = new JPanel(new GridLayout(2, 2, 6, 6));
        actionButtonGrid.setOpaque(false);

        undoBtn = createStyledButton("Undo Move", new Color(60, 65, 75));
        undoBtn.addActionListener(e -> {
            if (actionListener != null) actionListener.onUndo();
        });

        flipBtn = createStyledButton("Flip Board", new Color(60, 65, 75));
        flipBtn.addActionListener(e -> {
            if (actionListener != null) actionListener.onToggleFlip();
        });

        drawBtn = createStyledButton("Offer Draw", new Color(75, 70, 50));
        drawBtn.addActionListener(e -> {
            if (actionListener != null) actionListener.onOfferDraw();
        });

        resignBtn = createStyledButton("Resign", new Color(85, 45, 45));
        resignBtn.addActionListener(e -> {
            if (actionListener != null) actionListener.onResign();
        });

        actionButtonGrid.add(undoBtn);
        actionButtonGrid.add(flipBtn);
        actionButtonGrid.add(drawBtn);
        actionButtonGrid.add(resignBtn);
        add(actionButtonGrid);
        add(Box.createVerticalStrut(6));

        newGameBtn = createStyledButton("Start New Game", new Color(40, 110, 70));
        newGameBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        newGameBtn.addActionListener(e -> {
            if (actionListener != null) actionListener.onNewGame();
        });
        add(newGameBtn);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleTimeControlChange() {
        if (actionListener == null) return;
        int idx = timeControlCombo.getSelectedIndex();
        switch (idx) {
            case 0 -> actionListener.onTimeModeChanged(0, 0); // Untimed
            case 1 -> actionListener.onTimeModeChanged(3, 0); // 3m
            case 2 -> actionListener.onTimeModeChanged(5, 0); // 5m
            case 3 -> actionListener.onTimeModeChanged(10, 0); // 10m
            case 4 -> actionListener.onTimeModeChanged(15, 10); // 15 | 10
            case 5 -> actionListener.onTimeModeChanged(30, 0); // 30m
        }
    }

    public void setActionListener(GameActionListener listener) {
        this.actionListener = listener;
    }

    public ClockPanel getClockPanel() {
        return clockPanel;
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
        return moveHistoryPanel;
    }

    public void updateStatus(PieceColor turn, GameState state, boolean inCheck) {
        if (state.isGameOver()) {
            turnStatusLabel.setText(state.getDescription());
            turnStatusLabel.setForeground(new Color(255, 100, 100));
            gameStateBanner.setText("Game Finished");
            undoBtn.setEnabled(true);
            drawBtn.setEnabled(false);
            resignBtn.setEnabled(false);
        } else {
            turnStatusLabel.setText(turn.getDisplayName() + "'s Turn");
            turnStatusLabel.setForeground(Color.WHITE);
            if (inCheck) {
                gameStateBanner.setText("Warning: " + turn.getDisplayName() + " King is in Check!");
                gameStateBanner.setForeground(new Color(255, 180, 70));
            } else {
                gameStateBanner.setText("Game in progress");
                gameStateBanner.setForeground(new Color(160, 165, 175));
            }
            drawBtn.setEnabled(true);
            resignBtn.setEnabled(true);
        }
    }

    public void updateCapturedPieces(List<Piece> blackPiecesLost, List<Piece> whitePiecesLost,
                                     int whiteAdvantage, int blackAdvantage) {
        topCapturedPanel.updateCaptured(blackPiecesLost, whiteAdvantage);
        bottomCapturedPanel.updateCaptured(whitePiecesLost, blackAdvantage);
    }
}
