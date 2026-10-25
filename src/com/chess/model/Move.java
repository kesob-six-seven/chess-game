package com.chess.model;

import java.util.Objects;

/**
 * Represents a move from one square to another, including any captures or special actions.
 */
public final class Move {
    private final Square from;
    private final Square to;
    private final Piece pieceMoved;
    private final Piece pieceCaptured;
    private final PieceType promotionPiece;
    private final MoveType type;
    private boolean isCheck;
    private boolean isCheckmate;
    private String sanNotation;

    public Move(Square from, Square to, Piece pieceMoved, Piece pieceCaptured, PieceType promotionPiece, MoveType type) {
        this.from = Objects.requireNonNull(from, "From square cannot be null");
        this.to = Objects.requireNonNull(to, "To square cannot be null");
        this.pieceMoved = Objects.requireNonNull(pieceMoved, "Moved piece cannot be null");
        this.pieceCaptured = pieceCaptured;
        this.promotionPiece = promotionPiece;
        this.type = Objects.requireNonNull(type, "MoveType cannot be null");
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public Piece getPieceCaptured() {
        return pieceCaptured;
    }

    public PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public MoveType getType() {
        return type;
    }

    public boolean isCapture() {
        return type == MoveType.CAPTURE || type == MoveType.EN_PASSANT || type == MoveType.PROMOTION_CAPTURE;
    }

    public boolean isCastling() {
        return type == MoveType.CASTLE_KINGSIDE || type == MoveType.CASTLE_QUEENSIDE;
    }

    public boolean isPromotion() {
        return type == MoveType.PROMOTION || type == MoveType.PROMOTION_CAPTURE;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        this.isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.isCheckmate = checkmate;
    }

    public String getSanNotation() {
        return sanNotation;
    }

    public void setSanNotation(String sanNotation) {
        this.sanNotation = sanNotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move move)) return false;
        return Objects.equals(from, move.from) &&
               Objects.equals(to, move.to) &&
               Objects.equals(pieceMoved, move.pieceMoved) &&
               promotionPiece == move.promotionPiece &&
               type == move.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, pieceMoved, promotionPiece, type);
    }

    @Override
    public String toString() {
        return sanNotation != null ? sanNotation : (from.toAlgebraic() + " -> " + to.toAlgebraic());
    }
}
