package com.chess.ui;

import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Renders custom high-fidelity vector chess pieces using Java 2D graphics.
 * Independent of system fonts or external asset files.
 */
public final class PiecePainter {

    private PiecePainter() {}

    public static void drawPiece(Graphics2D g, Piece piece, int x, int y, int size) {
        if (piece == null || size <= 0) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Translate and scale to normalized 100x100 space
        g2d.translate(x, y);
        double scale = size / 100.0;
        g2d.scale(scale, scale);

        boolean isWhite = piece.getColor() == PieceColor.WHITE;

        // Colors
        Color fillTop = isWhite ? new Color(255, 255, 255) : new Color(64, 64, 68);
        Color fillBottom = isWhite ? new Color(222, 222, 226) : new Color(26, 26, 28);
        Color outlineColor = isWhite ? new Color(38, 38, 42) : new Color(18, 18, 20);
        Color secondaryBorder = isWhite ? new Color(200, 200, 205) : new Color(160, 160, 168);
        Color accentLine = isWhite ? new Color(120, 120, 130) : new Color(110, 110, 120);

        LinearGradientPaint fillPaint = new LinearGradientPaint(
                50, 15, 50, 90,
                new float[]{0.0f, 1.0f},
                new Color[]{fillTop, fillBottom}
        );

        // Soft drop shadow beneath base
        g2d.setColor(new Color(0, 0, 0, 45));
        g2d.fill(new Ellipse2D.Double(22, 85, 56, 10));

        switch (piece.getType()) {
            case PAWN -> drawPawn(g2d, fillPaint, outlineColor, secondaryBorder, isWhite);
            case KNIGHT -> drawKnight(g2d, fillPaint, outlineColor, secondaryBorder, accentLine, isWhite);
            case BISHOP -> drawBishop(g2d, fillPaint, outlineColor, secondaryBorder, accentLine, isWhite);
            case ROOK -> drawRook(g2d, fillPaint, outlineColor, secondaryBorder, accentLine, isWhite);
            case QUEEN -> drawQueen(g2d, fillPaint, outlineColor, secondaryBorder, accentLine, isWhite);
            case KING -> drawKing(g2d, fillPaint, outlineColor, secondaryBorder, accentLine, isWhite);
        }

        g2d.dispose();
    }

    private static void drawPawn(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, boolean isWhite) {
        Path2D body = new Path2D.Double();
        // Base
        body.moveTo(25, 87);
        body.curveTo(25, 82, 32, 80, 36, 78);
        // Stem
        body.curveTo(42, 68, 43, 56, 42, 47);
        // Collar
        body.lineTo(58, 47);
        // Stem right
        body.curveTo(57, 56, 58, 68, 64, 78);
        body.curveTo(68, 80, 75, 82, 75, 87);
        body.closePath();

        // Fill body
        g.setPaint(fill);
        g.fill(body);

        // Head
        Ellipse2D head = new Ellipse2D.Double(35, 17, 30, 30);
        g.fill(head);

        // Collar ring
        RoundRectangle2D collar = new RoundRectangle2D.Double(36, 44, 28, 6, 4, 4);
        g.fill(collar);

        // Base plinth
        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(24, 84, 52, 6, 3, 3);
        g.fill(basePlinth);

        // Outlines
        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(head);
            g.draw(collar);
            g.draw(body);
            g.draw(basePlinth);
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(head);
        g.draw(collar);
        g.draw(body);
        g.draw(basePlinth);

        // Highlight glint on head
        g.setColor(isWhite ? new Color(255, 255, 255, 180) : new Color(255, 255, 255, 70));
        g.fill(new Ellipse2D.Double(41, 22, 9, 6));
    }

    private static void drawKnight(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, Color accent, boolean isWhite) {
        Path2D horse = new Path2D.Double();
        horse.moveTo(26, 86);
        horse.curveTo(26, 80, 31, 75, 34, 72);
        // Chest
        horse.curveTo(35, 62, 32, 54, 25, 46);
        // Muzzle
        horse.curveTo(22, 42, 23, 38, 27, 36);
        horse.curveTo(34, 34, 39, 36, 43, 33);
        // Forehead
        horse.lineTo(48, 21);
        // Ear tip
        horse.lineTo(54, 15);
        horse.lineTo(56, 23);
        // Mane curve
        horse.curveTo(68, 30, 75, 46, 73, 62);
        horse.curveTo(72, 70, 73, 78, 74, 86);
        horse.closePath();

        g.setPaint(fill);
        g.fill(horse);

        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(22, 84, 56, 6, 3, 3);
        g.fill(basePlinth);

        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(horse);
            g.draw(basePlinth);
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(horse);
        g.draw(basePlinth);

        // Details: Eye, muzzle slit, mane cuts
        g.setColor(accent);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Eye
        g.draw(new Ellipse2D.Double(38, 32, 4.5, 4.5));
        // Mouth
        Path2D mouth = new Path2D.Double();
        mouth.moveTo(25, 43);
        mouth.lineTo(34, 43);
        g.draw(mouth);
        // Mane details
        Path2D mane = new Path2D.Double();
        mane.moveTo(60, 32);
        mane.curveTo(64, 40, 64, 48, 62, 56);
        g.draw(mane);
    }

    private static void drawBishop(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, Color accent, boolean isWhite) {
        Path2D body = new Path2D.Double();
        body.moveTo(27, 86);
        body.curveTo(27, 80, 33, 76, 36, 72);
        body.curveTo(40, 62, 40, 52, 38, 44);
        body.lineTo(62, 44);
        body.curveTo(60, 52, 60, 62, 64, 72);
        body.curveTo(67, 76, 73, 80, 73, 86);
        body.closePath();

        // Mitre head
        Path2D mitre = new Path2D.Double();
        mitre.moveTo(35, 45);
        mitre.curveTo(32, 36, 40, 24, 50, 20);
        mitre.curveTo(60, 24, 68, 36, 65, 45);
        mitre.closePath();

        g.setPaint(fill);
        g.fill(body);
        g.fill(mitre);

        // Orb on top
        Ellipse2D topOrb = new Ellipse2D.Double(46.5, 14, 7, 7);
        g.fill(topOrb);

        RoundRectangle2D collar = new RoundRectangle2D.Double(35, 42, 30, 6, 3, 3);
        g.fill(collar);

        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(24, 84, 52, 6, 3, 3);
        g.fill(basePlinth);

        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(body);
            g.draw(mitre);
            g.draw(topOrb);
            g.draw(collar);
            g.draw(basePlinth);
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(body);
        g.draw(mitre);
        g.draw(topOrb);
        g.draw(collar);
        g.draw(basePlinth);

        // Mitre cut slash
        g.setColor(outline);
        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D slit = new Path2D.Double();
        slit.moveTo(48, 25);
        slit.lineTo(41, 37);
        g.draw(slit);
    }

    private static void drawRook(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, Color accent, boolean isWhite) {
        Path2D tower = new Path2D.Double();
        tower.moveTo(27, 86);
        tower.curveTo(27, 80, 32, 76, 34, 72);
        tower.lineTo(37, 43);
        tower.lineTo(63, 43);
        tower.lineTo(66, 72);
        tower.curveTo(68, 76, 73, 80, 73, 86);
        tower.closePath();

        // Battlements cornice
        Path2D crown = new Path2D.Double();
        crown.moveTo(27, 43);
        crown.lineTo(27, 26);
        crown.lineTo(36, 26);
        crown.lineTo(36, 33);
        crown.lineTo(44, 33);
        crown.lineTo(44, 26);
        crown.lineTo(56, 26);
        crown.lineTo(56, 33);
        crown.lineTo(64, 33);
        crown.lineTo(64, 26);
        crown.lineTo(73, 26);
        crown.lineTo(73, 43);
        crown.closePath();

        g.setPaint(fill);
        g.fill(tower);
        g.fill(crown);

        RoundRectangle2D waistRing = new RoundRectangle2D.Double(32, 42, 36, 5, 2, 2);
        g.fill(waistRing);

        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(24, 84, 52, 6, 3, 3);
        g.fill(basePlinth);

        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(tower);
            g.draw(crown);
            g.draw(waistRing);
            g.draw(basePlinth);
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(tower);
        g.draw(crown);
        g.draw(waistRing);
        g.draw(basePlinth);
    }

    private static void drawQueen(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, Color accent, boolean isWhite) {
        Path2D body = new Path2D.Double();
        body.moveTo(26, 86);
        body.curveTo(26, 80, 31, 76, 35, 72);
        body.curveTo(39, 60, 40, 52, 38, 46);
        body.lineTo(62, 46);
        body.curveTo(60, 52, 61, 60, 65, 72);
        body.curveTo(69, 76, 74, 80, 74, 86);
        body.closePath();

        // 5-Pointed Coronet
        Path2D crown = new Path2D.Double();
        crown.moveTo(31, 46);
        crown.lineTo(25, 28);
        crown.lineTo(37, 36);
        crown.lineTo(43, 23);
        crown.lineTo(50, 34);
        crown.lineTo(57, 23);
        crown.lineTo(63, 36);
        crown.lineTo(75, 28);
        crown.lineTo(69, 46);
        crown.closePath();

        g.setPaint(fill);
        g.fill(body);
        g.fill(crown);

        // 5 Crown pearls
        double[][] pearlCoords = {{25, 27}, {43, 22}, {57, 22}, {75, 27}};
        for (double[] coord : pearlCoords) {
            g.fill(new Ellipse2D.Double(coord[0] - 2.5, coord[1] - 2.5, 5.0, 5.0));
        }
        g.fill(new Ellipse2D.Double(47.5, 33, 5.0, 5.0));

        RoundRectangle2D waistRing = new RoundRectangle2D.Double(34, 44, 32, 5, 2, 2);
        g.fill(waistRing);

        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(23, 84, 54, 6, 3, 3);
        g.fill(basePlinth);

        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(body);
            g.draw(crown);
            g.draw(waistRing);
            g.draw(basePlinth);
            for (double[] coord : pearlCoords) {
                g.draw(new Ellipse2D.Double(coord[0] - 2.5, coord[1] - 2.5, 5.0, 5.0));
            }
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(body);
        g.draw(crown);
        g.draw(waistRing);
        g.draw(basePlinth);
        for (double[] coord : pearlCoords) {
            g.draw(new Ellipse2D.Double(coord[0] - 2.5, coord[1] - 2.5, 5.0, 5.0));
        }
        g.draw(new Ellipse2D.Double(47.5, 33, 5.0, 5.0));
    }

    private static void drawKing(Graphics2D g, LinearGradientPaint fill, Color outline, Color secBorder, Color accent, boolean isWhite) {
        Path2D body = new Path2D.Double();
        body.moveTo(26, 86);
        body.curveTo(26, 80, 31, 76, 35, 72);
        body.curveTo(39, 60, 40, 52, 38, 46);
        body.lineTo(62, 46);
        body.curveTo(60, 52, 61, 60, 65, 72);
        body.curveTo(69, 76, 74, 80, 74, 86);
        body.closePath();

        // Arched regal crown mantle
        Path2D crown = new Path2D.Double();
        crown.moveTo(33, 46);
        crown.curveTo(25, 34, 38, 28, 50, 32);
        crown.curveTo(62, 28, 75, 34, 67, 46);
        crown.closePath();

        // Regal cross on top
        Path2D cross = new Path2D.Double();
        // Vertical post
        cross.append(new Rectangle2D.Double(48, 16, 4, 15), false);
        // Horizontal bar
        cross.append(new Rectangle2D.Double(44, 20, 12, 4), false);

        g.setPaint(fill);
        g.fill(body);
        g.fill(crown);
        g.fill(cross);

        RoundRectangle2D waistRing = new RoundRectangle2D.Double(34, 44, 32, 5, 2, 2);
        g.fill(waistRing);

        RoundRectangle2D basePlinth = new RoundRectangle2D.Double(23, 84, 54, 6, 3, 3);
        g.fill(basePlinth);

        if (!isWhite) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(secBorder);
            g.draw(body);
            g.draw(crown);
            g.draw(cross);
            g.draw(waistRing);
            g.draw(basePlinth);
        }

        g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(body);
        g.draw(crown);
        g.draw(cross);
        g.draw(waistRing);
        g.draw(basePlinth);
    }
}
