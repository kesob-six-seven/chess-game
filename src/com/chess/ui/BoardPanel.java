package com.chess.ui;

import com.chess.model.Board;
import com.chess.model.Move;
import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;
import com.chess.model.Square;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive 8x8 Chess Board component supporting click-to-move,
 * drag-and-drop, custom piece rendering, coordinate labels, themes, and perspective flipping.
 */
public class BoardPanel extends JPanel {
    public interface MoveEventListener {
        void onPlayerMove(Move move);
        PieceType requestPromotionChoice(PieceColor color);
    }

    private Board board;
    private ChessTheme theme = ChessTheme.WOOD;
    private boolean flipped = false;

    private Square selectedSquare = null;
    private List<Move> legalMovesForSelected = new ArrayList<>();
    private Move lastMove = null;
    private Square hoveredSquare = null;

    // Drag-and-drop state
    private boolean isDragging = false;
    private Square dragOrigin = null;
    private Piece draggingPiece = null;
    private Point dragCurrentPoint = null;

    private MoveEventListener moveListener;

    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(640, 640));
        setMinimumSize(new Dimension(400, 400));
        setBackground(new Color(24, 25, 28));

        BoardMouseListener mouseHandler = new BoardMouseListener();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setBoard(Board board) {
        this.board = board;
        this.selectedSquare = null;
        this.legalMovesForSelected.clear();
        this.lastMove = null;
        this.hoveredSquare = null;
        this.isDragging = false;
        repaint();
    }

    public void setTheme(ChessTheme theme) {
        this.theme = theme;
        repaint();
    }

    public ChessTheme getTheme() {
        return theme;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        repaint();
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void toggleFlip() {
        setFlipped(!flipped);
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
        repaint();
    }

    public void setMoveListener(MoveEventListener listener) {
        this.moveListener = listener;
    }

    public void clearSelection() {
        selectedSquare = null;
        legalMovesForSelected.clear();
        isDragging = false;
        dragOrigin = null;
        draggingPiece = null;
        repaint();
    }

    private int getBoardMargin() {
        return 24; // Space around board for rank/file notation
    }

    private int getBoardSize() {
        int margin = getBoardMargin();
        int available = Math.min(getWidth(), getHeight()) - (margin * 2);
        return Math.max(0, (available / 8) * 8); // nearest multiple of 8
    }

    private int getSquareSize() {
        return getBoardSize() / 8;
    }

    private int getBoardStartX() {
        return (getWidth() - getBoardSize()) / 2;
    }

    private int getBoardStartY() {
        return (getHeight() - getBoardSize()) / 2;
    }

    private Square screenToSquare(int x, int y) {
        int boardX = getBoardStartX();
        int boardY = getBoardStartY();
        int squareSize = getSquareSize();
        if (squareSize <= 0) return null;

        int c = (x - boardX) / squareSize;
        int r = (y - boardY) / squareSize;

        if (c < 0 || c >= 8 || r < 0 || r >= 8) return null;

        int row = flipped ? 7 - r : r;
        int col = flipped ? 7 - c : c;

        return Square.isValid(row, col) ? Square.of(row, col) : null;
    }

    private Point squareToScreen(Square sq) {
        int boardX = getBoardStartX();
        int boardY = getBoardStartY();
        int squareSize = getSquareSize();

        int r = flipped ? 7 - sq.getRow() : sq.getRow();
        int c = flipped ? 7 - sq.getCol() : sq.getCol();

        return new Point(boardX + (c * squareSize), boardY + (r * squareSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int startX = getBoardStartX();
        int startY = getBoardStartY();
        int squareSize = getSquareSize();
        int boardSize = getBoardSize();

        if (squareSize <= 0) {
            g2d.dispose();
            return;
        }

        // Draw outer board border/bezel
        g2d.setColor(new Color(36, 38, 44));
        g2d.fillRoundRect(startX - 18, startY - 18, boardSize + 36, boardSize + 36, 12, 12);
        g2d.setColor(new Color(50, 53, 62));
        g2d.drawRoundRect(startX - 18, startY - 18, boardSize + 36, boardSize + 36, 12, 12);

        // 1. Draw Squares
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int row = flipped ? 7 - r : r;
                int col = flipped ? 7 - c : c;
                Square sq = Square.of(row, col);

                int sx = startX + (c * squareSize);
                int sy = startY + (r * squareSize);

                boolean isLight = sq.isLight();
                g2d.setColor(isLight ? theme.getLightSquare() : theme.getDarkSquare());
                g2d.fillRect(sx, sy, squareSize, squareSize);

                // Highlight last move squares
                if (lastMove != null && (sq.equals(lastMove.getFrom()) || sq.equals(lastMove.getTo()))) {
                    g2d.setColor(theme.getLastMoveColor(isLight));
                    g2d.fillRect(sx, sy, squareSize, squareSize);
                }

                // Highlight selected square
                if (selectedSquare != null && selectedSquare.equals(sq)) {
                    g2d.setColor(new Color(255, 235, 80, 160));
                    g2d.fillRect(sx, sy, squareSize, squareSize);
                }

                // Highlight in-check king
                if (board.isInCheck(board.getTurn())) {
                    Square kingSq = board.findKing(board.getTurn());
                    if (sq.equals(kingSq)) {
                        float radius = squareSize * 0.7f;
                        RadialGradientPaint checkGlow = new RadialGradientPaint(
                                sx + (squareSize / 2.0f), sy + (squareSize / 2.0f), radius,
                                new float[]{0.0f, 0.7f, 1.0f},
                                new Color[]{theme.getCheckHighlight(), new Color(230, 40, 40, 100), new Color(230, 40, 40, 0)}
                        );
                        g2d.setPaint(checkGlow);
                        g2d.fillRect(sx, sy, squareSize, squareSize);
                    }
                }
            }
        }

        // 2. Draw Rank and File coordinates
        drawCoordinates(g2d, startX, startY, squareSize);

        // 3. Draw Legal Move indicators
        drawLegalMoveIndicators(g2d, squareSize);

        // 4. Draw Pieces on board (skip currently dragged piece)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square sq = Square.of(r, c);
                if (isDragging && sq.equals(dragOrigin)) {
                    continue; // Will be drawn floating at cursor
                }
                Piece piece = board.getPiece(sq);
                if (piece != null) {
                    Point p = squareToScreen(sq);
                    PiecePainter.drawPiece(g2d, piece, p.x, p.y, squareSize);
                }
            }
        }

        // 5. Draw Dragged Piece (floating with shadow)
        if (isDragging && draggingPiece != null && dragCurrentPoint != null) {
            int px = dragCurrentPoint.x - (squareSize / 2);
            int py = dragCurrentPoint.y - (squareSize / 2);

            // Elevated shadow
            g2d.setColor(new Color(0, 0, 0, 70));
            g2d.fill(new Ellipse2D.Double(px + (squareSize * 0.2), py + (squareSize * 0.8), squareSize * 0.6, 12));

            // Piece
            PiecePainter.drawPiece(g2d, draggingPiece, px, py - 4, squareSize);
        }

        g2d.dispose();
    }

    private void drawCoordinates(Graphics2D g2d, int startX, int startY, int squareSize) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(new Color(150, 155, 168));

        for (int i = 0; i < 8; i++) {
            // Rank labels (left and right)
            int rank = flipped ? (i + 1) : (8 - i);
            String rankStr = String.valueOf(rank);
            int yPos = startY + (i * squareSize) + (squareSize / 2) + (fm.getAscent() / 2) - 2;

            g2d.drawString(rankStr, startX - 14, yPos);
            g2d.drawString(rankStr, startX + (8 * squareSize) + 5, yPos);

            // File labels (top and bottom)
            char fileChar = (char) (flipped ? ('h' - i) : ('a' + i));
            String fileStr = String.valueOf(fileChar);
            int xPos = startX + (i * squareSize) + (squareSize / 2) - (fm.stringWidth(fileStr) / 2);

            g2d.drawString(fileStr, xPos, startY - 5);
            g2d.drawString(fileStr, xPos, startY + (8 * squareSize) + 14);
        }
    }

    private void drawLegalMoveIndicators(Graphics2D g2d, int squareSize) {
        if (legalMovesForSelected.isEmpty()) return;

        for (Move move : legalMovesForSelected) {
            Square to = move.getTo();
            Point p = squareToScreen(to);
            boolean isCapture = move.isCapture();

            if (isCapture) {
                // Hollow ring around edge of target capture square
                g2d.setColor(new Color(0, 0, 0, 75));
                g2d.setStroke(new BasicStroke(squareSize * 0.08f));
                int pad = (int) (squareSize * 0.06);
                g2d.drawOval(p.x + pad, p.y + pad, squareSize - (pad * 2), squareSize - (pad * 2));
            } else {
                // Semi-transparent centered dot
                int dotSize = (int) (squareSize * 0.32);
                int dx = p.x + (squareSize - dotSize) / 2;
                int dy = p.y + (squareSize - dotSize) / 2;
                g2d.setColor(new Color(0, 0, 0, 65));
                g2d.fillOval(dx, dy, dotSize, dotSize);
            }
        }
    }

    private void handleSquareSelected(Square sq) {
        Piece clickedPiece = board.getPiece(sq);

        // If a piece is already selected and we click a legal target square
        if (selectedSquare != null) {
            Move matchedMove = findLegalMove(selectedSquare, sq);
            if (matchedMove != null) {
                executePlayerMove(matchedMove);
                return;
            }
        }

        // If clicking own piece, select it and show legal moves
        if (clickedPiece != null && clickedPiece.getColor() == board.getTurn()) {
            selectedSquare = sq;
            legalMovesForSelected = board.getLegalMovesForSquare(sq);
        } else {
            selectedSquare = null;
            legalMovesForSelected.clear();
        }
        repaint();
    }

    private Move findLegalMove(Square from, Square to) {
        for (Move m : legalMovesForSelected) {
            if (m.getFrom().equals(from) && m.getTo().equals(to)) {
                return m;
            }
        }
        return null;
    }

    private void executePlayerMove(Move move) {
        // Check for pawn promotion
        if (move.isPromotion()) {
            PieceType choice = PieceType.QUEEN;
            if (moveListener != null) {
                choice = moveListener.requestPromotionChoice(move.getPieceMoved().getColor());
            }
            // Find specific move with this promotion type
            for (Move m : legalMovesForSelected) {
                if (m.getTo().equals(move.getTo()) && m.getPromotionPiece() == choice) {
                    move = m;
                    break;
                }
            }
        }

        selectedSquare = null;
        legalMovesForSelected.clear();
        isDragging = false;
        dragOrigin = null;
        draggingPiece = null;

        if (moveListener != null) {
            moveListener.onPlayerMove(move);
        }
        repaint();
    }

    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                clearSelection();
                return;
            }

            Square sq = screenToSquare(e.getX(), e.getY());
            if (sq == null) return;

            Piece piece = board.getPiece(sq);

            // Check if clicking on an already selected piece's destination
            if (selectedSquare != null && findLegalMove(selectedSquare, sq) != null) {
                handleSquareSelected(sq);
                return;
            }

            // Start drag if clicking own piece
            if (piece != null && piece.getColor() == board.getTurn()) {
                selectedSquare = sq;
                legalMovesForSelected = board.getLegalMovesForSquare(sq);
                isDragging = true;
                dragOrigin = sq;
                draggingPiece = piece;
                dragCurrentPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                repaint();
            } else {
                handleSquareSelected(sq);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) return;

            setCursor(Cursor.getDefaultCursor());
            if (isDragging && dragOrigin != null) {
                Square dropSquare = screenToSquare(e.getX(), e.getY());
                if (dropSquare != null && !dropSquare.equals(dragOrigin)) {
                    Move move = findLegalMove(dragOrigin, dropSquare);
                    if (move != null) {
                        executePlayerMove(move);
                        return;
                    }
                }
                // If dropped on same square or invalid square, retain selection
                isDragging = false;
                draggingPiece = null;
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDragging) {
                dragCurrentPoint = e.getPoint();
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Square sq = screenToSquare(e.getX(), e.getY());
            if (sq != hoveredSquare) {
                hoveredSquare = sq;
                if (sq != null && board.getPiece(sq) != null && board.getPiece(sq).getColor() == board.getTurn()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else if (selectedSquare != null && sq != null && findLegalMove(selectedSquare, sq) != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }
}
