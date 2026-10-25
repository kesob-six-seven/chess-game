package com.chess.model;

import java.util.Objects;

/**
 * Represents a chess piece with a type and color.
 */
public final class Piece {
    private final PieceType type;
    private final PieceColor color;

    public Piece(PieceType type, PieceColor color) {
        this.type = Objects.requireNonNull(type, "PieceType cannot be null");
        this.color = Objects.requireNonNull(color, "PieceColor cannot be null");
    }

    public PieceType getType() {
        return type;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean isWhite() {
        return color == PieceColor.WHITE;
    }

    public boolean isBlack() {
        return color == PieceColor.BLACK;
    }

    public int getValue() {
        return type.getValue();
    }

    public String getUnicode() {
        return type.getUnicode(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        return type == piece.type && color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return color.name().charAt(0) + "" + type.getSymbol();
    }
}
