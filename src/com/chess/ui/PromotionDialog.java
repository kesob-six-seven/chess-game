package com.chess.ui;

import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

/**
 * Visual modal dialog allowing the player to select their pawn promotion piece.
 */
public class PromotionDialog extends JDialog {
    private PieceType selectedPieceType = PieceType.QUEEN;

    public PromotionDialog(JFrame parent, PieceColor color) {
        super(parent, "Pawn Promotion", true);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65), 2),
                BorderFactory.createLineBorder(new Color(240, 240, 245), 1)
        ));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(38, 40, 44));
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Promote Pawn to:", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        content.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        buttonPanel.setOpaque(false);

        PieceType[] choices = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        for (PieceType type : choices) {
            JButton btn = createPieceButton(type, color);
            buttonPanel.add(btn);
        }

        content.add(buttonPanel, BorderLayout.CENTER);
        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);
    }

    private JButton createPieceButton(PieceType type, PieceColor color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(75, 80, 90));
                } else {
                    g2d.setColor(new Color(50, 52, 58));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                Piece piece = new Piece(type, color);
                PiecePainter.drawPiece(g2d, piece, 8, 8, 64);
            }
        };

        btn.setPreferredSize(new Dimension(80, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(type.getDisplayName());

        btn.addActionListener(e -> {
            selectedPieceType = type;
            dispose();
        });

        return btn;
    }

    public PieceType getSelectedPieceType() {
        return selectedPieceType;
    }
}
