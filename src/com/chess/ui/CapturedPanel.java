package com.chess.ui;

import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Renders miniature captured piece icons along with material advantage (+N).
 */
public class CapturedPanel extends JPanel {
    private final PieceColor playerColor;
    private final List<Piece> capturedPieces = new ArrayList<>();
    private int materialAdvantage = 0;

    public CapturedPanel(PieceColor playerColor) {
        this.playerColor = playerColor;
        setPreferredSize(new Dimension(300, 36));
        setMinimumSize(new Dimension(200, 32));
        setOpaque(false);
    }

    public void updateCaptured(List<Piece> piecesCapturedByOpponent, int materialDiff) {
        // piecesCapturedByOpponent are pieces this player lost.
        // What we want to show for this player is the pieces they HAVE captured from their opponent!
        // So for White, show opponent's Black pieces that were captured.
        this.capturedPieces.clear();
        this.capturedPieces.addAll(piecesCapturedByOpponent);
        // Sort pieces by value: Queen first, then Rook, Bishop, Knight, Pawn
        this.capturedPieces.sort(Comparator.comparingInt(Piece::getValue).reversed());
        this.materialAdvantage = materialDiff;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int iconSize = 24;
        int overlapX = 16; // Slight horizontal stacking
        int startX = 4;
        int y = (getHeight() - iconSize) / 2;

        int currentX = startX;
        for (Piece p : capturedPieces) {
            PiecePainter.drawPiece(g2d, p, currentX, y, iconSize);
            currentX += overlapX;
        }

        // Draw advantage badge if positive
        if (materialAdvantage > 0) {
            currentX += 12;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String badge = "+" + materialAdvantage;
            FontMetrics fm = g2d.getFontMetrics();
            int badgeW = fm.stringWidth(badge) + 10;
            int badgeH = 18;
            int badgeY = (getHeight() - badgeH) / 2;

            g2d.setColor(new Color(60, 65, 75));
            g2d.fillRoundRect(currentX, badgeY, badgeW, badgeH, 6, 6);
            g2d.setColor(new Color(220, 225, 235));
            g2d.drawString(badge, currentX + 5, badgeY + fm.getAscent() + 1);
        }

        g2d.dispose();
    }
}
