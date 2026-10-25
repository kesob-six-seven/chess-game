package com.chess.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the sequence of moves made in the game, supports undo/redo,
 * and maintains position counts for threefold repetition.
 */
public class MoveHistory {
    public static class HistoryEntry {
        private final Move move;
        private final Board boardSnapshot;
        private final String positionKey;

        public HistoryEntry(Move move, Board boardSnapshot, String positionKey) {
            this.move = move;
            this.boardSnapshot = boardSnapshot;
            this.positionKey = positionKey;
        }

        public Move getMove() {
            return move;
        }

        public Board getBoardSnapshot() {
            return boardSnapshot;
        }

        public String getPositionKey() {
            return positionKey;
        }
    }

    private final List<HistoryEntry> moves = new ArrayList<>();
    private final List<HistoryEntry> undoneMoves = new ArrayList<>();
    private final Map<String, Integer> positionCounts = new HashMap<>();

    public MoveHistory() {}

    public void recordMove(Move move, Board boardBefore, String newPositionKey) {
        moves.add(new HistoryEntry(move, boardBefore, newPositionKey));
        undoneMoves.clear();
        positionCounts.put(newPositionKey, positionCounts.getOrDefault(newPositionKey, 0) + 1);
    }

    public boolean canUndo() {
        return !moves.isEmpty();
    }

    public boolean canRedo() {
        return !undoneMoves.isEmpty();
    }

    public HistoryEntry undo() {
        if (!canUndo()) return null;
        HistoryEntry lastEntry = moves.remove(moves.size() - 1);
        undoneMoves.add(lastEntry);

        // Decrement position count
        int count = positionCounts.getOrDefault(lastEntry.getPositionKey(), 1);
        if (count <= 1) {
            positionCounts.remove(lastEntry.getPositionKey());
        } else {
            positionCounts.put(lastEntry.getPositionKey(), count - 1);
        }

        return lastEntry;
    }

    public HistoryEntry redo() {
        if (!canRedo()) return null;
        HistoryEntry redoEntry = undoneMoves.remove(undoneMoves.size() - 1);
        moves.add(redoEntry);
        positionCounts.put(redoEntry.getPositionKey(), positionCounts.getOrDefault(redoEntry.getPositionKey(), 0) + 1);
        return redoEntry;
    }

    public boolean isThreefoldRepetition(String currentKey) {
        return positionCounts.getOrDefault(currentKey, 0) >= 3;
    }

    public List<HistoryEntry> getEntries() {
        return Collections.unmodifiableList(moves);
    }

    public Move getLastMove() {
        return moves.isEmpty() ? null : moves.get(moves.size() - 1).getMove();
    }

    public int size() {
        return moves.size();
    }

    public void clear() {
        moves.clear();
        undoneMoves.clear();
        positionCounts.clear();
    }

    /**
     * Returns captured pieces for a given color (i.e. pieces lost by that color).
     */
    public List<Piece> getCapturedPieces(PieceColor color) {
        List<Piece> captured = new ArrayList<>();
        for (HistoryEntry entry : moves) {
            Piece cap = entry.getMove().getPieceCaptured();
            if (cap != null && cap.getColor() == color) {
                captured.add(cap);
            }
        }
        return captured;
    }
}
