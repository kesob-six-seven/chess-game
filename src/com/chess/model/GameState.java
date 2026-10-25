package com.chess.model;

/**
 * Represents the current status/outcome of a chess game.
 */
public enum GameState {
    ACTIVE("Game in progress"),
    CHECK("Check!"),
    CHECKMATE("Checkmate!"),
    STALEMATE("Draw by Stalemate"),
    DRAW_INSUFFICIENT_MATERIAL("Draw by Insufficient Material"),
    DRAW_FIFTY_MOVES("Draw by 50-Move Rule"),
    DRAW_THREEFOLD_REPETITION("Draw by Threefold Repetition"),
    DRAW_AGREEMENT("Draw by Mutual Agreement"),
    RESIGNATION("Game Over by Resignation"),
    TIMEOUT("Game Over on Time");

    private final String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGameOver() {
        return this != ACTIVE && this != CHECK;
    }

    public boolean isDraw() {
        return this == STALEMATE ||
               this == DRAW_INSUFFICIENT_MATERIAL ||
               this == DRAW_FIFTY_MOVES ||
               this == DRAW_THREEFOLD_REPETITION ||
               this == DRAW_AGREEMENT;
    }
}
