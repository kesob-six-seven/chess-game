package com.chess.ui;

import java.awt.Color;

/**
 * Defines visual color palettes and themes for the chess board.
 */
public enum ChessTheme {
    WOOD("Classic Wood",
            new Color(240, 217, 181), new Color(181, 136, 99),
            new Color(205, 210, 106, 170), new Color(245, 246, 130, 170),
            new Color(235, 97, 80, 180)),

    GREEN("Tournament Green",
            new Color(238, 238, 210), new Color(118, 150, 86),
            new Color(186, 202, 68, 180), new Color(246, 246, 130, 180),
            new Color(231, 76, 60, 180)),

    SLATE("Modern Slate",
            new Color(195, 210, 222), new Color(74, 105, 132),
            new Color(100, 180, 220, 160), new Color(140, 215, 250, 170),
            new Color(231, 76, 60, 180)),

    MIDNIGHT("Midnight Dark",
            new Color(160, 160, 160), new Color(50, 52, 55),
            new Color(65, 140, 180, 170), new Color(110, 190, 235, 180),
            new Color(220, 50, 50, 200));

    private final String displayName;
    private final Color lightSquare;
    private final Color darkSquare;
    private final Color lastMoveLight;
    private final Color lastMoveDark;
    private final Color checkHighlight;

    ChessTheme(String displayName, Color lightSquare, Color darkSquare,
               Color lastMoveLight, Color lastMoveDark, Color checkHighlight) {
        this.displayName = displayName;
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
        this.lastMoveLight = lastMoveLight;
        this.lastMoveDark = lastMoveDark;
        this.checkHighlight = checkHighlight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getLightSquare() {
        return lightSquare;
    }

    public Color getDarkSquare() {
        return darkSquare;
    }

    public Color getLastMoveColor(boolean isLight) {
        return isLight ? lastMoveLight : lastMoveDark;
    }

    public Color getCheckHighlight() {
        return checkHighlight;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
