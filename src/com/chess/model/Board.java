package com.chess.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 8x8 chess board and encapsulates move generation,
 * rule enforcement, castling rights, and check/checkmate detection.
 */
public class Board {
    private final Piece[][] grid = new Piece[8][8];

    // Castling rights
    private boolean whiteKingMoved = false;
    private boolean whiteKingsideRookMoved = false;
    private boolean whiteQueensideRookMoved = false;

    private boolean blackKingMoved = false;
    private boolean blackKingsideRookMoved = false;
    private boolean blackQueensideRookMoved = false;

    // En passant target square (if a pawn just moved 2 squares)
    private Square enPassantTarget = null;

    // 50-move rule counter (resets on pawn advance or piece capture)
    private int halfmoveClock = 0;

    // Fullmove number (increments after Black moves)
    private int fullmoveNumber = 1;

    // Active player's turn
    private PieceColor turn = PieceColor.WHITE;

    public Board() {
        setupStandardPosition();
    }

    /**
     * Initializes the standard starting position of chess.
     */
    public void setupStandardPosition() {
        // Clear grid
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid[r][c] = null;
            }
        }

        // Black back rank (row 0 / Rank 8)
        grid[0][0] = new Piece(PieceType.ROOK, PieceColor.BLACK);
        grid[0][1] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        grid[0][2] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        grid[0][3] = new Piece(PieceType.QUEEN, PieceColor.BLACK);
        grid[0][4] = new Piece(PieceType.KING, PieceColor.BLACK);
        grid[0][5] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        grid[0][6] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        grid[0][7] = new Piece(PieceType.ROOK, PieceColor.BLACK);

        // Black pawns (row 1 / Rank 7)
        for (int c = 0; c < 8; c++) {
            grid[1][c] = new Piece(PieceType.PAWN, PieceColor.BLACK);
        }

        // White pawns (row 6 / Rank 2)
        for (int c = 0; c < 8; c++) {
            grid[6][c] = new Piece(PieceType.PAWN, PieceColor.WHITE);
        }

        // White back rank (row 7 / Rank 1)
        grid[7][0] = new Piece(PieceType.ROOK, PieceColor.WHITE);
        grid[7][1] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        grid[7][2] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        grid[7][3] = new Piece(PieceType.QUEEN, PieceColor.WHITE);
        grid[7][4] = new Piece(PieceType.KING, PieceColor.WHITE);
        grid[7][5] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        grid[7][6] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        grid[7][7] = new Piece(PieceType.ROOK, PieceColor.WHITE);

        whiteKingMoved = false;
        whiteKingsideRookMoved = false;
        whiteQueensideRookMoved = false;
        blackKingMoved = false;
        blackKingsideRookMoved = false;
        blackQueensideRookMoved = false;

        enPassantTarget = null;
        halfmoveClock = 0;
        fullmoveNumber = 1;
        turn = PieceColor.WHITE;
    }

    public Piece getPiece(int row, int col) {
        if (!Square.isValid(row, col)) return null;
        return grid[row][col];
    }

    public Piece getPiece(Square sq) {
        return sq != null ? getPiece(sq.getRow(), sq.getCol()) : null;
    }

    public void setPiece(int row, int col, Piece piece) {
        if (Square.isValid(row, col)) {
            grid[row][col] = piece;
        }
    }

    public void setPiece(Square sq, Piece piece) {
        if (sq != null) {
            setPiece(sq.getRow(), sq.getCol(), piece);
        }
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void setTurn(PieceColor turn) {
        this.turn = turn;
    }

    /**
     * Replaces this board's state with a copy of the specified board.
     */
    public void copyFrom(Board other) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                this.grid[r][c] = other.grid[r][c];
            }
        }
        this.whiteKingMoved = other.whiteKingMoved;
        this.whiteKingsideRookMoved = other.whiteKingsideRookMoved;
        this.whiteQueensideRookMoved = other.whiteQueensideRookMoved;
        this.blackKingMoved = other.blackKingMoved;
        this.blackKingsideRookMoved = other.blackKingsideRookMoved;
        this.blackQueensideRookMoved = other.blackQueensideRookMoved;
        this.enPassantTarget = other.enPassantTarget;
        this.halfmoveClock = other.halfmoveClock;
        this.fullmoveNumber = other.fullmoveNumber;
        this.turn = other.turn;
    }

    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public boolean isWhiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean isWhiteKingsideRookMoved() {
        return whiteKingsideRookMoved;
    }

    public boolean isWhiteQueensideRookMoved() {
        return whiteQueensideRookMoved;
    }

    public boolean isBlackKingMoved() {
        return blackKingMoved;
    }

    public boolean isBlackKingsideRookMoved() {
        return blackKingsideRookMoved;
    }

    public boolean isBlackQueensideRookMoved() {
        return blackQueensideRookMoved;
    }

    /**
     * Creates an independent deep copy of this board.
     */
    public Board copy() {
        Board copy = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                copy.grid[r][c] = this.grid[r][c];
            }
        }
        copy.whiteKingMoved = this.whiteKingMoved;
        copy.whiteKingsideRookMoved = this.whiteKingsideRookMoved;
        copy.whiteQueensideRookMoved = this.whiteQueensideRookMoved;

        copy.blackKingMoved = this.blackKingMoved;
        copy.blackKingsideRookMoved = this.blackKingsideRookMoved;
        copy.blackQueensideRookMoved = this.blackQueensideRookMoved;

        copy.enPassantTarget = this.enPassantTarget;
        copy.halfmoveClock = this.halfmoveClock;
        copy.fullmoveNumber = this.fullmoveNumber;
        copy.turn = this.turn;
        return copy;
    }

    /**
     * Finds the current square of the King for the given color.
     */
    public Square findKing(PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return Square.of(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the square is attacked by any piece of the specified attacker color.
     */
    public boolean isSquareAttackedBy(Square sq, PieceColor attackerColor) {
        int r = sq.getRow();
        int c = sq.getCol();

        // 1. Pawn attacks
        int pawnRow = (attackerColor == PieceColor.WHITE) ? r + 1 : r - 1;
        if (Square.isValid(pawnRow, c - 1)) {
            Piece p = grid[pawnRow][c - 1];
            if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.PAWN) {
                return true;
            }
        }
        if (Square.isValid(pawnRow, c + 1)) {
            Piece p = grid[pawnRow][c + 1];
            if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.PAWN) {
                return true;
            }
        }

        // 2. Knight attacks
        int[][] knightOffsets = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] offset : knightOffsets) {
            int nr = r + offset[0];
            int nc = c + offset[1];
            if (Square.isValid(nr, nc)) {
                Piece p = grid[nr][nc];
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KNIGHT) {
                    return true;
                }
            }
        }

        // 3. Orthogonal rays (Rook or Queen)
        int[][] orthogonalDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : orthogonalDirs) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            while (Square.isValid(nr, nc)) {
                Piece p = grid[nr][nc];
                if (p != null) {
                    if (p.getColor() == attackerColor &&
                        (p.getType() == PieceType.ROOK || p.getType() == PieceType.QUEEN)) {
                        return true;
                    }
                    break; // Blocked
                }
                nr += dir[0];
                nc += dir[1];
            }
        }

        // 4. Diagonal rays (Bishop or Queen)
        int[][] diagonalDirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : diagonalDirs) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            while (Square.isValid(nr, nc)) {
                Piece p = grid[nr][nc];
                if (p != null) {
                    if (p.getColor() == attackerColor &&
                        (p.getType() == PieceType.BISHOP || p.getType() == PieceType.QUEEN)) {
                        return true;
                    }
                    break; // Blocked
                }
                nr += dir[0];
                nc += dir[1];
            }
        }

        // 5. King adjacent squares
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (Square.isValid(nr, nc)) {
                    Piece p = grid[nr][nc];
                    if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KING) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines whether the King of the specified color is currently in check.
     */
    public boolean isInCheck(PieceColor color) {
        Square kingSquare = findKing(color);
        if (kingSquare == null) return false;
        return isSquareAttackedBy(kingSquare, color.opposite());
    }

    /**
     * Returns all strictly legal moves for the given color.
     */
    public List<Move> getLegalMoves(PieceColor color) {
        List<Move> pseudoMoves = generatePseudoLegalMoves(color);
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : pseudoMoves) {
            Board testBoard = this.copy();
            testBoard.applyMove(move);
            if (!testBoard.isInCheck(color)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Returns legal moves starting from a specific square.
     */
    public List<Move> getLegalMovesForSquare(Square from) {
        Piece piece = getPiece(from);
        if (piece == null || piece.getColor() != turn) {
            return Collections.emptyList();
        }

        List<Move> allLegal = getLegalMoves(turn);
        List<Move> moves = new ArrayList<>();
        for (Move m : allLegal) {
            if (m.getFrom().equals(from)) {
                moves.add(m);
            }
        }
        return moves;
    }

    /**
     * Generates all pseudo-legal moves (not checking for self-check).
     */
    public List<Move> generatePseudoLegalMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getColor() == color) {
                    Square from = Square.of(r, c);
                    switch (piece.getType()) {
                        case PAWN -> generatePawnMoves(from, piece, moves);
                        case KNIGHT -> generateKnightMoves(from, piece, moves);
                        case BISHOP -> generateBishopMoves(from, piece, moves);
                        case ROOK -> generateRookMoves(from, piece, moves);
                        case QUEEN -> generateQueenMoves(from, piece, moves);
                        case KING -> generateKingMoves(from, piece, moves);
                    }
                }
            }
        }
        return moves;
    }

    private void generatePawnMoves(Square from, Piece piece, List<Move> moves) {
        int r = from.getRow();
        int c = from.getCol();
        int forwardDir = (piece.getColor() == PieceColor.WHITE) ? -1 : 1;
        int startRow = (piece.getColor() == PieceColor.WHITE) ? 6 : 1;
        int promoRow = (piece.getColor() == PieceColor.WHITE) ? 0 : 7;

        // 1. One step forward
        int oneForward = r + forwardDir;
        if (Square.isValid(oneForward, c) && grid[oneForward][c] == null) {
            Square to = Square.of(oneForward, c);
            if (oneForward == promoRow) {
                addPromotionMoves(from, to, piece, null, moves);
            } else {
                moves.add(new Move(from, to, piece, null, null, MoveType.NORMAL));

                // 2. Two steps forward from start rank
                int twoForward = r + (2 * forwardDir);
                if (r == startRow && grid[twoForward][c] == null) {
                    moves.add(new Move(from, Square.of(twoForward, c), piece, null, null, MoveType.NORMAL));
                }
            }
        }

        // 3. Diagonal captures
        int[] captureCols = {c - 1, c + 1};
        for (int capCol : captureCols) {
            if (Square.isValid(oneForward, capCol)) {
                Square to = Square.of(oneForward, capCol);
                Piece targetPiece = grid[oneForward][capCol];

                // Normal capture
                if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
                    if (oneForward == promoRow) {
                        addPromotionMoves(from, to, piece, targetPiece, moves);
                    } else {
                        moves.add(new Move(from, to, piece, targetPiece, null, MoveType.CAPTURE));
                    }
                }
                // En passant capture
                else if (to.equals(enPassantTarget)) {
                    int capturedPawnRow = r;
                    Piece epPiece = grid[capturedPawnRow][capCol];
                    if (epPiece != null && epPiece.getType() == PieceType.PAWN && epPiece.getColor() != piece.getColor()) {
                        moves.add(new Move(from, to, piece, epPiece, null, MoveType.EN_PASSANT));
                    }
                }
            }
        }
    }

    private void addPromotionMoves(Square from, Square to, Piece piece, Piece captured, List<Move> moves) {
        PieceType[] promoTypes = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        MoveType type = (captured != null) ? MoveType.PROMOTION_CAPTURE : MoveType.PROMOTION;
        for (PieceType pt : promoTypes) {
            moves.add(new Move(from, to, piece, captured, pt, type));
        }
    }

    private void generateKnightMoves(Square from, Piece piece, List<Move> moves) {
        int r = from.getRow();
        int c = from.getCol();
        int[][] offsets = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] off : offsets) {
            int nr = r + off[0];
            int nc = c + off[1];
            if (Square.isValid(nr, nc)) {
                Piece target = grid[nr][nc];
                Square to = Square.of(nr, nc);
                if (target == null) {
                    moves.add(new Move(from, to, piece, null, null, MoveType.NORMAL));
                } else if (target.getColor() != piece.getColor()) {
                    moves.add(new Move(from, to, piece, target, null, MoveType.CAPTURE));
                }
            }
        }
    }

    private void generateBishopMoves(Square from, Piece piece, List<Move> moves) {
        int[][] dirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        generateSlidingMoves(from, piece, dirs, moves);
    }

    private void generateRookMoves(Square from, Piece piece, List<Move> moves) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        generateSlidingMoves(from, piece, dirs, moves);
    }

    private void generateQueenMoves(Square from, Piece piece, List<Move> moves) {
        int[][] dirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        generateSlidingMoves(from, piece, dirs, moves);
    }

    private void generateSlidingMoves(Square from, Piece piece, int[][] dirs, List<Move> moves) {
        int r = from.getRow();
        int c = from.getCol();

        for (int[] dir : dirs) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            while (Square.isValid(nr, nc)) {
                Piece target = grid[nr][nc];
                Square to = Square.of(nr, nc);
                if (target == null) {
                    moves.add(new Move(from, to, piece, null, null, MoveType.NORMAL));
                } else {
                    if (target.getColor() != piece.getColor()) {
                        moves.add(new Move(from, to, piece, target, null, MoveType.CAPTURE));
                    }
                    break; // Blocked by piece
                }
                nr += dir[0];
                nc += dir[1];
            }
        }
    }

    private void generateKingMoves(Square from, Piece piece, List<Move> moves) {
        int r = from.getRow();
        int c = from.getCol();

        // 1. Regular 1-step moves
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (Square.isValid(nr, nc)) {
                    Piece target = grid[nr][nc];
                    Square to = Square.of(nr, nc);
                    if (target == null) {
                        moves.add(new Move(from, to, piece, null, null, MoveType.NORMAL));
                    } else if (target.getColor() != piece.getColor()) {
                        moves.add(new Move(from, to, piece, target, null, MoveType.CAPTURE));
                    }
                }
            }
        }

        // 2. Castling moves
        boolean isWhite = (piece.getColor() == PieceColor.WHITE);
        int kingRow = isWhite ? 7 : 0;
        boolean kingMoved = isWhite ? whiteKingMoved : blackKingMoved;
        boolean ksRookMoved = isWhite ? whiteKingsideRookMoved : blackKingsideRookMoved;
        boolean qsRookMoved = isWhite ? whiteQueensideRookMoved : blackQueensideRookMoved;
        PieceColor oppColor = piece.getColor().opposite();

        if (r == kingRow && c == 4 && !kingMoved) {
            // Kingside castling (O-O)
            if (!ksRookMoved) {
                Piece rook = grid[kingRow][7];
                if (rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == piece.getColor()) {
                    // Squares between king and rook must be empty: (kingRow, 5) and (kingRow, 6)
                    if (grid[kingRow][5] == null && grid[kingRow][6] == null) {
                        // King must not be in check, and transit square / destination square must not be attacked
                        if (!isSquareAttackedBy(Square.of(kingRow, 4), oppColor) &&
                            !isSquareAttackedBy(Square.of(kingRow, 5), oppColor) &&
                            !isSquareAttackedBy(Square.of(kingRow, 6), oppColor)) {
                            moves.add(new Move(from, Square.of(kingRow, 6), piece, null, null, MoveType.CASTLE_KINGSIDE));
                        }
                    }
                }
            }

            // Queenside castling (O-O-O)
            if (!qsRookMoved) {
                Piece rook = grid[kingRow][0];
                if (rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == piece.getColor()) {
                    // Squares between king and rook must be empty: (kingRow, 1), (kingRow, 2), (kingRow, 3)
                    if (grid[kingRow][1] == null && grid[kingRow][2] == null && grid[kingRow][3] == null) {
                        // King must not be in check, and transit squares (3 and 2) must not be attacked
                        if (!isSquareAttackedBy(Square.of(kingRow, 4), oppColor) &&
                            !isSquareAttackedBy(Square.of(kingRow, 3), oppColor) &&
                            !isSquareAttackedBy(Square.of(kingRow, 2), oppColor)) {
                            moves.add(new Move(from, Square.of(kingRow, 2), piece, null, null, MoveType.CASTLE_QUEENSIDE));
                        }
                    }
                }
            }
        }
    }

    /**
     * Executes a move on this board without SAN computation (internal apply).
     */
    public void applyMove(Move move) {
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece movedPiece = move.getPieceMoved();

        // 1. Move piece to destination
        grid[from.getRow()][from.getCol()] = null;

        // Reset or set en passant target for next ply
        Square prevEp = enPassantTarget;
        enPassantTarget = null;

        if (move.getType() == MoveType.EN_PASSANT) {
            // Clear the captured pawn's square
            int capturedPawnRow = from.getRow();
            int capturedPawnCol = to.getCol();
            grid[capturedPawnRow][capturedPawnCol] = null;
            grid[to.getRow()][to.getCol()] = movedPiece;
        } else if (move.isCastling()) {
            grid[to.getRow()][to.getCol()] = movedPiece;
            int r = to.getRow();
            if (move.getType() == MoveType.CASTLE_KINGSIDE) {
                // Rook moves from col 7 to col 5
                Piece rook = grid[r][7];
                grid[r][7] = null;
                grid[r][5] = rook;
            } else {
                // Rook moves from col 0 to col 3
                Piece rook = grid[r][0];
                grid[r][0] = null;
                grid[r][3] = rook;
            }
        } else if (move.isPromotion()) {
            Piece promoPiece = new Piece(move.getPromotionPiece(), movedPiece.getColor());
            grid[to.getRow()][to.getCol()] = promoPiece;
        } else {
            grid[to.getRow()][to.getCol()] = movedPiece;

            // Check if double pawn push (set en passant target)
            if (movedPiece.getType() == PieceType.PAWN && Math.abs(to.getRow() - from.getRow()) == 2) {
                int intermediateRow = (from.getRow() + to.getRow()) / 2;
                enPassantTarget = Square.of(intermediateRow, from.getCol());
            }
        }

        // 2. Update castling rights
        if (movedPiece.getType() == PieceType.KING) {
            if (movedPiece.getColor() == PieceColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (movedPiece.getType() == PieceType.ROOK) {
            if (from.equals(Square.of(7, 0))) whiteQueensideRookMoved = true;
            else if (from.equals(Square.of(7, 7))) whiteKingsideRookMoved = true;
            else if (from.equals(Square.of(0, 0))) blackQueensideRookMoved = true;
            else if (from.equals(Square.of(0, 7))) blackKingsideRookMoved = true;
        }

        // If a rook was captured on its starting corner, update rights
        if (to.equals(Square.of(7, 0))) whiteQueensideRookMoved = true;
        else if (to.equals(Square.of(7, 7))) whiteKingsideRookMoved = true;
        else if (to.equals(Square.of(0, 0))) blackQueensideRookMoved = true;
        else if (to.equals(Square.of(0, 7))) blackKingsideRookMoved = true;

        // 3. Update halfmove clock (50-move rule)
        if (movedPiece.getType() == PieceType.PAWN || move.isCapture()) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        // 4. Update fullmove number and turn
        if (turn == PieceColor.BLACK) {
            fullmoveNumber++;
        }
        turn = turn.opposite();
    }

    /**
     * Executes a legal move, updates SAN notation, and checks check/checkmate flags.
     */
    public void makeMove(Move move) {
        // Generate SAN notation before altering board
        String san = computeSAN(move);
        applyMove(move);

        boolean inCheck = isInCheck(turn);
        boolean hasLegalMoves = !getLegalMoves(turn).isEmpty();

        if (inCheck && !hasLegalMoves) {
            move.setCheckmate(true);
            move.setCheck(true);
            move.setSanNotation(san + "#");
        } else if (inCheck) {
            move.setCheck(true);
            move.setSanNotation(san + "+");
        } else {
            move.setSanNotation(san);
        }
    }

    /**
     * Computes the Standard Algebraic Notation (SAN) for a move before it is applied.
     */
    private String computeSAN(Move move) {
        if (move.getType() == MoveType.CASTLE_KINGSIDE) {
            return "O-O";
        }
        if (move.getType() == MoveType.CASTLE_QUEENSIDE) {
            return "O-O-O";
        }

        StringBuilder sb = new StringBuilder();
        Piece piece = move.getPieceMoved();
        Square from = move.getFrom();
        Square to = move.getTo();

        if (piece.getType() == PieceType.PAWN) {
            if (move.isCapture()) {
                sb.append(from.getFile()).append('x');
            }
            sb.append(to.toAlgebraic());
            if (move.isPromotion()) {
                sb.append('=').append(move.getPromotionPiece().getSymbol());
            }
        } else {
            sb.append(piece.getType().getSymbol());

            // Disambiguation
            List<Move> legalMoves = getLegalMoves(piece.getColor());
            boolean needFile = false;
            boolean needRank = false;

            for (Move other : legalMoves) {
                if (other.equals(move)) continue;
                if (other.getTo().equals(to) &&
                    other.getPieceMoved().getType() == piece.getType()) {
                    if (other.getFrom().getCol() != from.getCol()) {
                        needFile = true;
                    } else if (other.getFrom().getRow() != from.getRow()) {
                        needRank = true;
                    }
                }
            }

            if (needFile) {
                sb.append(from.getFile());
            }
            if (needRank) {
                sb.append(from.getRank());
            }

            if (move.isCapture()) {
                sb.append('x');
            }
            sb.append(to.toAlgebraic());
        }

        return sb.toString();
    }

    /**
     * Checks if current position is insufficient material for checkmate.
     */
    public boolean isInsufficientMaterial() {
        int whitePawns = 0, blackPawns = 0;
        int whiteRooks = 0, blackRooks = 0;
        int whiteQueens = 0, blackQueens = 0;
        int whiteKnights = 0, blackKnights = 0;
        int whiteBishops = 0, blackBishops = 0;
        Square whiteBishopSq = null, blackBishopSq = null;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p == null || p.getType() == PieceType.KING) continue;

                if (p.getColor() == PieceColor.WHITE) {
                    switch (p.getType()) {
                        case PAWN -> whitePawns++;
                        case ROOK -> whiteRooks++;
                        case QUEEN -> whiteQueens++;
                        case KNIGHT -> whiteKnights++;
                        case BISHOP -> {
                            whiteBishops++;
                            whiteBishopSq = Square.of(r, c);
                        }
                        default -> {}
                    }
                } else {
                    switch (p.getType()) {
                        case PAWN -> blackPawns++;
                        case ROOK -> blackRooks++;
                        case QUEEN -> blackQueens++;
                        case KNIGHT -> blackKnights++;
                        case BISHOP -> {
                            blackBishops++;
                            blackBishopSq = Square.of(r, c);
                        }
                        default -> {}
                    }
                }
            }
        }

        // If any pawns, rooks, or queens exist, checkmate is possible
        if (whitePawns > 0 || blackPawns > 0 ||
            whiteRooks > 0 || blackRooks > 0 ||
            whiteQueens > 0 || blackQueens > 0) {
            return false;
        }

        int whiteMinors = whiteKnights + whiteBishops;
        int blackMinors = blackKnights + blackBishops;

        // K vs K
        if (whiteMinors == 0 && blackMinors == 0) return true;

        // K+B vs K or K+N vs K
        if ((whiteMinors == 1 && blackMinors == 0) || (whiteMinors == 0 && blackMinors == 1)) {
            return true;
        }

        // K+B vs K+B (same color bishops)
        if (whiteBishops == 1 && whiteKnights == 0 &&
            blackBishops == 1 && blackKnights == 0) {
            if (whiteBishopSq != null && blackBishopSq != null) {
                return whiteBishopSq.isLight() == blackBishopSq.isLight();
            }
        }

        return false;
    }

    /**
     * Checks the 50-move rule (100 half-moves without pawn move or capture).
     */
    public boolean isFiftyMoveRule() {
        return halfmoveClock >= 100;
    }

    /**
     * Generates a FEN-like position key for threefold repetition detection.
     */
    public String getPositionKey() {
        StringBuilder sb = new StringBuilder();
        // Pieces
        for (int r = 0; r < 8; r++) {
            int empty = 0;
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    char ch = p.getType().getSymbol();
                    sb.append(p.getColor() == PieceColor.WHITE ? ch : Character.toLowerCase(ch));
                }
            }
            if (empty > 0) sb.append(empty);
            if (r < 7) sb.append('/');
        }

        // Side to move
        sb.append(' ').append(turn == PieceColor.WHITE ? 'w' : 'b');

        // Castling rights
        sb.append(' ');
        boolean anyCastle = false;
        if (!whiteKingMoved) {
            if (!whiteKingsideRookMoved) { sb.append('K'); anyCastle = true; }
            if (!whiteQueensideRookMoved) { sb.append('Q'); anyCastle = true; }
        }
        if (!blackKingMoved) {
            if (!blackKingsideRookMoved) { sb.append('k'); anyCastle = true; }
            if (!blackQueensideRookMoved) { sb.append('q'); anyCastle = true; }
        }
        if (!anyCastle) sb.append('-');

        // En passant
        sb.append(' ');
        if (enPassantTarget != null) {
            sb.append(enPassantTarget.toAlgebraic());
        } else {
            sb.append('-');
        }

        return sb.toString();
    }

    /**
     * Calculates the total material value on board for a player.
     */
    public int getMaterialScore(PieceColor color) {
        int total = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor() == color) {
                    total += p.getValue();
                }
            }
        }
        return total;
    }
}
