package com.chess.model;

/**
 * Represents the two colors in a chess game.
 */
public enum PieceColor {
    WHITE,
    BLACK;

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public String getDisplayName() {
        return this == WHITE ? "White" : "Black";
    }
}
