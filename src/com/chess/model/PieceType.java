package com.chess.model;

/**
 * Represents the type of chess piece.
 */
public enum PieceType {
    PAWN('P', 1, "Pawn", "\u2659", "\u265F"),
    KNIGHT('N', 3, "Knight", "\u2658", "\u265E"),
    BISHOP('B', 3, "Bishop", "\u2657", "\u265D"),
    ROOK('R', 5, "Rook", "\u2656", "\u265C"),
    QUEEN('Q', 9, "Queen", "\u2655", "\u265B"),
    KING('K', 0, "King", "\u2654", "\u265A");

    private final char symbol;
    private final int value;
    private final String displayName;
    private final String whiteUnicode;
    private final String blackUnicode;

    PieceType(char symbol, int value, String displayName, String whiteUnicode, String blackUnicode) {
        this.symbol = symbol;
        this.value = value;
        this.displayName = displayName;
        this.whiteUnicode = whiteUnicode;
        this.blackUnicode = blackUnicode;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnicode(PieceColor color) {
        return color == PieceColor.WHITE ? whiteUnicode : blackUnicode;
    }
}
