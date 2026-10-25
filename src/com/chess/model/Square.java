package com.chess.model;

import java.util.Objects;

/**
 * Represents a single square on an 8x8 chessboard.
 * row: 0 to 7 (0 is rank 8, 7 is rank 1)
 * col: 0 to 7 (0 is file a, 7 is file h)
 */
public final class Square {
    private final int row;
    private final int col;

    // Cache of all 64 board squares
    private static final Square[][] CACHE = new Square[8][8];

    static {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                CACHE[r][c] = new Square(r, c);
            }
        }
    }

    private Square(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static Square of(int row, int col) {
        if (!isValid(row, col)) {
            throw new IllegalArgumentException("Square out of bounds: (" + row + ", " + col + ")");
        }
        return CACHE[row][col];
    }

    public static Square fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid algebraic square: " + notation);
        }
        char fileChar = Character.toLowerCase(notation.charAt(0));
        char rankChar = notation.charAt(1);

        int col = fileChar - 'a';
        int rank = rankChar - '1';
        int row = 7 - rank;

        return of(row, col);
    }

    public static boolean isValid(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRank() {
        return 8 - row;
    }

    public char getFile() {
        return (char) ('a' + col);
    }

    public boolean isLight() {
        return (row + col) % 2 == 0;
    }

    public String toAlgebraic() {
        return "" + getFile() + getRank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square square)) return false;
        return row == square.row && col == square.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}
