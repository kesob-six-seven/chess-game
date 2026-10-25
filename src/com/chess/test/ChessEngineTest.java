package com.chess.test;

import com.chess.model.Board;
import com.chess.model.Move;
import com.chess.model.MoveType;
import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;
import com.chess.model.Square;

import java.util.List;

public class ChessEngineTest {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== RUNNING CHESS ENGINE VERIFICATION SUITE ===");

        testInitialBoardSetup();
        testInitialLegalMovesCount();
        testPawnMovesAndDoublePush();
        testEnPassant();
        testPawnPromotion();
        testCastlingKingsideAndQueenside();
        testCastlingBlockedWhenInCheckOrThroughCheck();
        testFoolsMate();
        testScholarsMate();
        testStalemateDetection();
        testInsufficientMaterial();
        testFiftyMoveRule();

        System.out.println("===============================================");
        System.out.printf("RESULTS: %d / %d tests passed.%n", testsPassed, testsRun);
        if (testsPassed == testsRun) {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! :)");
        } else {
            System.err.println("SOME TESTS FAILED! :(");
            System.exit(1);
        }
    }

    private static void assertTrue(String message, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + message);
        } else {
            System.err.println("  [FAIL] " + message);
        }
    }

    private static void testInitialBoardSetup() {
        Board board = new Board();
        assertTrue("White King is on e1",
                board.getPiece(Square.fromAlgebraic("e1")) != null &&
                board.getPiece(Square.fromAlgebraic("e1")).getType() == PieceType.KING &&
                board.getPiece(Square.fromAlgebraic("e1")).getColor() == PieceColor.WHITE);

        assertTrue("Black King is on e8",
                board.getPiece(Square.fromAlgebraic("e8")) != null &&
                board.getPiece(Square.fromAlgebraic("e8")).getType() == PieceType.KING &&
                board.getPiece(Square.fromAlgebraic("e8")).getColor() == PieceColor.BLACK);

        assertTrue("White Queen is on d1",
                board.getPiece(Square.fromAlgebraic("d1")) != null &&
                board.getPiece(Square.fromAlgebraic("d1")).getType() == PieceType.QUEEN &&
                board.getPiece(Square.fromAlgebraic("d1")).getColor() == PieceColor.WHITE);

        assertTrue("e4 square is initially empty",
                board.getPiece(Square.fromAlgebraic("e4")) == null);
    }

    private static void testInitialLegalMovesCount() {
        Board board = new Board();
        List<Move> legalMoves = board.getLegalMoves(PieceColor.WHITE);
        // In standard chess, White has exactly 20 initial legal moves (16 pawn moves + 4 knight moves)
        assertTrue("White has exactly 20 initial legal moves", legalMoves.size() == 20);
    }

    private static void testPawnMovesAndDoublePush() {
        Board board = new Board();
        Square e2 = Square.fromAlgebraic("e2");
        Square e4 = Square.fromAlgebraic("e4");

        Move e2e4 = findMove(board, e2, e4);
        assertTrue("e2 to e4 double push is a legal move", e2e4 != null);

        board.makeMove(e2e4);
        assertTrue("e4 now contains White pawn",
                board.getPiece(e4) != null && board.getPiece(e4).getType() == PieceType.PAWN);
        assertTrue("e2 is now empty", board.getPiece(e2) == null);
        assertTrue("En passant target is e3",
                board.getEnPassantTarget() != null && board.getEnPassantTarget().equals(Square.fromAlgebraic("e3")));
        assertTrue("Active turn is now Black", board.getTurn() == PieceColor.BLACK);
    }

    private static void testEnPassant() {
        Board board = new Board();
        // 1. e4 Nf6
        board.makeMove(findMove(board, Square.fromAlgebraic("e2"), Square.fromAlgebraic("e4")));
        board.makeMove(findMove(board, Square.fromAlgebraic("g8"), Square.fromAlgebraic("f6")));
        // 2. e5 d5
        board.makeMove(findMove(board, Square.fromAlgebraic("e4"), Square.fromAlgebraic("e5")));
        board.makeMove(findMove(board, Square.fromAlgebraic("d7"), Square.fromAlgebraic("d5")));

        // White pawn on e5 can capture en passant to d6
        Move epMove = findMove(board, Square.fromAlgebraic("e5"), Square.fromAlgebraic("d6"));
        assertTrue("En passant move e5xd6 exists", epMove != null && epMove.getType() == MoveType.EN_PASSANT);

        board.makeMove(epMove);
        assertTrue("White pawn is on d6 after en passant",
                board.getPiece(Square.fromAlgebraic("d6")) != null &&
                board.getPiece(Square.fromAlgebraic("d6")).getType() == PieceType.PAWN &&
                board.getPiece(Square.fromAlgebraic("d6")).getColor() == PieceColor.WHITE);
        assertTrue("Black pawn on d5 was captured and removed",
                board.getPiece(Square.fromAlgebraic("d5")) == null);
    }

    private static void testPawnPromotion() {
        Board board = new Board();
        // Clear board and place White pawn on e7 and Black king on h8, White king on a1
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.setPiece(r, c, null);
            }
        }
        board.setPiece(Square.fromAlgebraic("e7"), new Piece(PieceType.PAWN, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("a1"), new Piece(PieceType.KING, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("h8"), new Piece(PieceType.KING, PieceColor.BLACK));
        board.setTurn(PieceColor.WHITE);

        List<Move> legalMoves = board.getLegalMoves(PieceColor.WHITE);
        boolean hasQueenPromo = false;
        boolean hasKnightPromo = false;

        for (Move m : legalMoves) {
            if (m.getTo().equals(Square.fromAlgebraic("e8"))) {
                if (m.getPromotionPiece() == PieceType.QUEEN) hasQueenPromo = true;
                if (m.getPromotionPiece() == PieceType.KNIGHT) hasKnightPromo = true;
            }
        }

        assertTrue("Pawn promotion provides Queen option", hasQueenPromo);
        assertTrue("Pawn promotion provides Knight option", hasKnightPromo);

        Move promoQueen = null;
        for (Move m : legalMoves) {
            if (m.getTo().equals(Square.fromAlgebraic("e8")) && m.getPromotionPiece() == PieceType.QUEEN) {
                promoQueen = m;
                break;
            }
        }
        board.makeMove(promoQueen);
        assertTrue("Square e8 now has a White Queen",
                board.getPiece(Square.fromAlgebraic("e8")) != null &&
                board.getPiece(Square.fromAlgebraic("e8")).getType() == PieceType.QUEEN &&
                board.getPiece(Square.fromAlgebraic("e8")).getColor() == PieceColor.WHITE);
    }

    private static void testCastlingKingsideAndQueenside() {
        Board board = new Board();
        // 1. e4 e5
        board.makeMove(findMove(board, Square.fromAlgebraic("e2"), Square.fromAlgebraic("e4")));
        board.makeMove(findMove(board, Square.fromAlgebraic("e7"), Square.fromAlgebraic("e5")));
        // 2. Nf3 Nc6
        board.makeMove(findMove(board, Square.fromAlgebraic("g1"), Square.fromAlgebraic("f3")));
        board.makeMove(findMove(board, Square.fromAlgebraic("b8"), Square.fromAlgebraic("c6")));
        // 3. Bc4 Bc5
        board.makeMove(findMove(board, Square.fromAlgebraic("f1"), Square.fromAlgebraic("c4")));
        board.makeMove(findMove(board, Square.fromAlgebraic("f8"), Square.fromAlgebraic("c5")));

        // White kingside castle (e1 -> g1) should now be available
        Move castleKingside = findMove(board, Square.fromAlgebraic("e1"), Square.fromAlgebraic("g1"));
        assertTrue("White kingside castling (O-O) is legal", castleKingside != null && castleKingside.getType() == MoveType.CASTLE_KINGSIDE);

        board.makeMove(castleKingside);
        assertTrue("King is on g1 after castling",
                board.getPiece(Square.fromAlgebraic("g1")) != null &&
                board.getPiece(Square.fromAlgebraic("g1")).getType() == PieceType.KING);
        assertTrue("Rook moved to f1 after kingside castling",
                board.getPiece(Square.fromAlgebraic("f1")) != null &&
                board.getPiece(Square.fromAlgebraic("f1")).getType() == PieceType.ROOK);
        assertTrue("e1 is empty", board.getPiece(Square.fromAlgebraic("e1")) == null);
        assertTrue("h1 is empty", board.getPiece(Square.fromAlgebraic("h1")) == null);
    }

    private static void testCastlingBlockedWhenInCheckOrThroughCheck() {
        Board board = new Board();
        // Clear board and place White King on e1, White Rook on h1, Black Rook on e8 (checking e1)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.setPiece(r, c, null);
            }
        }
        board.setPiece(Square.fromAlgebraic("e1"), new Piece(PieceType.KING, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("h1"), new Piece(PieceType.ROOK, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("a8"), new Piece(PieceType.KING, PieceColor.BLACK));
        board.setPiece(Square.fromAlgebraic("e8"), new Piece(PieceType.ROOK, PieceColor.BLACK));
        board.setTurn(PieceColor.WHITE);

        assertTrue("White king is in check", board.isInCheck(PieceColor.WHITE));
        Move castleMove = findMove(board, Square.fromAlgebraic("e1"), Square.fromAlgebraic("g1"));
        assertTrue("Castling is illegal while in check", castleMove == null);

        // Move Black rook to f8 (attacking f1 transit square)
        board.setPiece(Square.fromAlgebraic("e8"), null);
        board.setPiece(Square.fromAlgebraic("f8"), new Piece(PieceType.ROOK, PieceColor.BLACK));
        assertTrue("White king is no longer in check", !board.isInCheck(PieceColor.WHITE));
        castleMove = findMove(board, Square.fromAlgebraic("e1"), Square.fromAlgebraic("g1"));
        assertTrue("Castling through attacked square f1 is illegal", castleMove == null);
    }

    private static void testFoolsMate() {
        Board board = new Board();
        // 1. f3 e5
        board.makeMove(findMove(board, Square.fromAlgebraic("f2"), Square.fromAlgebraic("f3")));
        board.makeMove(findMove(board, Square.fromAlgebraic("e7"), Square.fromAlgebraic("e5")));
        // 2. g4 Qh4#
        board.makeMove(findMove(board, Square.fromAlgebraic("g2"), Square.fromAlgebraic("g4")));
        Move qh4 = findMove(board, Square.fromAlgebraic("d8"), Square.fromAlgebraic("h4"));
        assertTrue("Black Queen to h4 exists", qh4 != null);

        board.makeMove(qh4);
        assertTrue("Fool's Mate SAN ends with #", qh4.getSanNotation().endsWith("#"));
        assertTrue("White is in checkmate", board.isInCheck(PieceColor.WHITE) && board.getLegalMoves(PieceColor.WHITE).isEmpty());
    }

    private static void testScholarsMate() {
        Board board = new Board();
        // 1. e4 e5
        board.makeMove(findMove(board, Square.fromAlgebraic("e2"), Square.fromAlgebraic("e4")));
        board.makeMove(findMove(board, Square.fromAlgebraic("e7"), Square.fromAlgebraic("e5")));
        // 2. Qh5 Nc6
        board.makeMove(findMove(board, Square.fromAlgebraic("d1"), Square.fromAlgebraic("h5")));
        board.makeMove(findMove(board, Square.fromAlgebraic("b8"), Square.fromAlgebraic("c6")));
        // 3. Bc4 Nf6
        board.makeMove(findMove(board, Square.fromAlgebraic("f1"), Square.fromAlgebraic("c4")));
        board.makeMove(findMove(board, Square.fromAlgebraic("g8"), Square.fromAlgebraic("f6")));
        // 4. Qxf7#
        Move qxf7 = findMove(board, Square.fromAlgebraic("h5"), Square.fromAlgebraic("f7"));
        assertTrue("Scholar's Mate Qxf7 move exists", qxf7 != null);

        board.makeMove(qxf7);
        assertTrue("Scholar's Mate SAN is Qxf7#", qxf7.getSanNotation().equals("Qxf7#"));
        assertTrue("Black is in checkmate", board.isInCheck(PieceColor.BLACK) && board.getLegalMoves(PieceColor.BLACK).isEmpty());
    }

    private static void testStalemateDetection() {
        Board board = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.setPiece(r, c, null);
            }
        }
        // Black King on a8, White Queen on c7, White King on a6
        board.setPiece(Square.fromAlgebraic("a8"), new Piece(PieceType.KING, PieceColor.BLACK));
        board.setPiece(Square.fromAlgebraic("c7"), new Piece(PieceType.QUEEN, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("a6"), new Piece(PieceType.KING, PieceColor.WHITE));
        board.setTurn(PieceColor.BLACK);

        assertTrue("Black King is not in check", !board.isInCheck(PieceColor.BLACK));
        assertTrue("Black has no legal moves", board.getLegalMoves(PieceColor.BLACK).isEmpty());
        // Since Black is not in check and has no legal moves -> Stalemate!
    }

    private static void testInsufficientMaterial() {
        Board board = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board.setPiece(r, c, null);
            }
        }
        // K vs K
        board.setPiece(Square.fromAlgebraic("e1"), new Piece(PieceType.KING, PieceColor.WHITE));
        board.setPiece(Square.fromAlgebraic("e8"), new Piece(PieceType.KING, PieceColor.BLACK));
        assertTrue("K vs K is insufficient material", board.isInsufficientMaterial());

        // K+N vs K
        board.setPiece(Square.fromAlgebraic("g1"), new Piece(PieceType.KNIGHT, PieceColor.WHITE));
        assertTrue("K+N vs K is insufficient material", board.isInsufficientMaterial());

        // Add a pawn -> sufficient material
        board.setPiece(Square.fromAlgebraic("e2"), new Piece(PieceType.PAWN, PieceColor.WHITE));
        assertTrue("K+N+P vs K is sufficient material", !board.isInsufficientMaterial());
    }

    private static void testFiftyMoveRule() {
        Board board = new Board();
        assertTrue("Initially halfmove clock is 0", board.getHalfmoveClock() == 0);
        assertTrue("Initially not fifty-move rule", !board.isFiftyMoveRule());

        // Simulate 50 moves (100 plies) with knight shuffling back and forth
        // e.g. Nf3 Nf6, Ng1 Ng8
        for (int i = 0; i < 25; i++) {
            board.makeMove(findMove(board, Square.fromAlgebraic("g1"), Square.fromAlgebraic("f3")));
            board.makeMove(findMove(board, Square.fromAlgebraic("g8"), Square.fromAlgebraic("f6")));
            board.makeMove(findMove(board, Square.fromAlgebraic("f3"), Square.fromAlgebraic("g1")));
            board.makeMove(findMove(board, Square.fromAlgebraic("f6"), Square.fromAlgebraic("g8")));
        }
        assertTrue("Halfmove clock reached 100", board.getHalfmoveClock() == 100);
        assertTrue("Fifty-move rule triggered", board.isFiftyMoveRule());
    }

    private static Move findMove(Board board, Square from, Square to) {
        for (Move m : board.getLegalMoves(board.getTurn())) {
            if (m.getFrom().equals(from) && m.getTo().equals(to)) {
                return m;
            }
        }
        return null;
    }
}
