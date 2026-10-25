package com.chess.controller;

import com.chess.audio.SoundManager;
import com.chess.model.Board;
import com.chess.model.GameState;
import com.chess.model.Move;
import com.chess.model.MoveHistory;
import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;
import com.chess.ui.BoardPanel;
import com.chess.ui.ChessFrame;
import com.chess.ui.ChessTheme;
import com.chess.ui.InfoPanel;
import com.chess.ui.PromotionDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Orchestrates gameplay logic, move execution, rule checks, sound playback,
 * clocks, and UI updates for local 1v1 chess games.
 */
public class GameController implements BoardPanel.MoveEventListener, InfoPanel.GameActionListener, com.chess.ui.ClockPanel.TimeoutListener {
    private final Board board;
    private final MoveHistory history;
    private final SoundManager soundManager;
    private GameState gameState = GameState.ACTIVE;

    private ChessFrame frame;
    private BoardPanel boardPanel;
    private InfoPanel infoPanel;

    // Optional setting: flip board automatically each turn for hotseat 1v1
    private boolean autoFlipBoard = false;

    public GameController() {
        this.board = new Board();
        this.history = new MoveHistory();
        this.soundManager = new SoundManager();
    }

    public void setViews(ChessFrame frame, BoardPanel boardPanel, InfoPanel infoPanel) {
        this.frame = frame;
        this.boardPanel = boardPanel;
        this.infoPanel = infoPanel;

        boardPanel.setMoveListener(this);
        infoPanel.setActionListener(this);
        infoPanel.getClockPanel().setTimeoutListener(this);

        updateFullUI();
    }

    public Board getBoard() {
        return board;
    }

    public MoveHistory getHistory() {
        return history;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public boolean isAutoFlipBoard() {
        return autoFlipBoard;
    }

    public void setAutoFlipBoard(boolean autoFlipBoard) {
        this.autoFlipBoard = autoFlipBoard;
    }

    @Override
    public void onPlayerMove(Move move) {
        if (gameState.isGameOver()) return;

        Board boardBefore = board.copy();
        board.makeMove(move);

        String posKey = board.getPositionKey();
        history.recordMove(move, boardBefore, posKey);

        // Sound effects
        if (move.isCheckmate()) {
            soundManager.playGameOverSound();
        } else if (move.isCheck()) {
            soundManager.playCheckSound();
        } else if (move.isCastling()) {
            soundManager.playCastleSound();
        } else if (move.isCapture()) {
            soundManager.playCaptureSound();
        } else {
            soundManager.playMoveSound();
        }

        // Check game end conditions
        determineGameState();

        // Switch active clock & start timer on first move
        infoPanel.getClockPanel().setActiveTurn(board.getTurn());
        infoPanel.getClockPanel().start();

        if (autoFlipBoard && !gameState.isGameOver()) {
            boardPanel.setFlipped(board.getTurn() == PieceColor.BLACK);
        }

        boardPanel.setLastMove(move);
        updateFullUI();

        if (gameState.isGameOver()) {
            infoPanel.getClockPanel().stop();
            showGameOverDialog();
        }
    }

    @Override
    public PieceType requestPromotionChoice(PieceColor color) {
        PromotionDialog dialog = new PromotionDialog(frame, color);
        dialog.setVisible(true);
        return dialog.getSelectedPieceType();
    }

    private void determineGameState() {
        PieceColor currentTurn = board.getTurn();
        boolean inCheck = board.isInCheck(currentTurn);
        List<Move> legalMoves = board.getLegalMoves(currentTurn);

        if (legalMoves.isEmpty()) {
            if (inCheck) {
                gameState = GameState.CHECKMATE;
            } else {
                gameState = GameState.STALEMATE;
            }
        } else if (board.isFiftyMoveRule()) {
            gameState = GameState.DRAW_FIFTY_MOVES;
        } else if (board.isInsufficientMaterial()) {
            gameState = GameState.DRAW_INSUFFICIENT_MATERIAL;
        } else if (history.isThreefoldRepetition(board.getPositionKey())) {
            gameState = GameState.DRAW_THREEFOLD_REPETITION;
        } else if (inCheck) {
            gameState = GameState.CHECK;
        } else {
            gameState = GameState.ACTIVE;
        }
    }

    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            String title = "Game Finished";
            String msg;
            int type = JOptionPane.INFORMATION_MESSAGE;

            if (gameState == GameState.CHECKMATE) {
                PieceColor winner = board.getTurn().opposite();
                msg = "Checkmate! " + winner.getDisplayName() + " wins the game!";
            } else if (gameState == GameState.RESIGNATION) {
                PieceColor winner = board.getTurn().opposite();
                msg = winner.getDisplayName() + " wins by resignation!";
            } else if (gameState == GameState.TIMEOUT) {
                PieceColor winner = board.getTurn().opposite();
                msg = winner.getDisplayName() + " wins on time!";
            } else {
                msg = gameState.getDescription();
            }

            JOptionPane.showMessageDialog(frame, msg, title, type);
        });
    }

    @Override
    public void onNewGame() {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Start a new game? Current match progress will be reset.",
                "New Game Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            resetGame();
        }
    }

    public void resetGame() {
        board.setupStandardPosition();
        history.clear();
        gameState = GameState.ACTIVE;
        boardPanel.setBoard(board);
        infoPanel.getMoveHistoryPanel().clear();
        infoPanel.getClockPanel().reset();
        infoPanel.getClockPanel().setActiveTurn(PieceColor.WHITE);
        if (autoFlipBoard) {
            boardPanel.setFlipped(false);
        }
        updateFullUI();
    }

    @Override
    public void onUndo() {
        if (!history.canUndo()) {
            JOptionPane.showMessageDialog(frame, "No moves available to undo.", "Undo Move", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MoveHistory.HistoryEntry undone = history.undo();
        if (undone != null) {
            // Restore board snapshot
            Board snapshot = undone.getBoardSnapshot();
            board.copyFrom(snapshot);

            gameState = GameState.ACTIVE;
            boardPanel.setLastMove(history.getLastMove());
            infoPanel.getClockPanel().setActiveTurn(board.getTurn());
            if (autoFlipBoard) {
                boardPanel.setFlipped(board.getTurn() == PieceColor.BLACK);
            }
            updateFullUI();
        }
    }

    @Override
    public void onOfferDraw() {
        if (gameState.isGameOver()) return;

        PieceColor offerer = board.getTurn();
        PieceColor respondent = offerer.opposite();

        int result = JOptionPane.showConfirmDialog(
                frame,
                offerer.getDisplayName() + " offers a draw.\nDoes " + respondent.getDisplayName() + " accept?",
                "Draw Offer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            gameState = GameState.DRAW_AGREEMENT;
            infoPanel.getClockPanel().stop();
            soundManager.playGameOverSound();
            updateFullUI();
            JOptionPane.showMessageDialog(frame, "Game ended in a draw by mutual agreement.", "Draw", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void onResign() {
        if (gameState.isGameOver()) return;

        PieceColor resigner = board.getTurn();
        int result = JOptionPane.showConfirmDialog(
                frame,
                resigner.getDisplayName() + ", are you sure you want to resign?",
                "Resign Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            gameState = GameState.RESIGNATION;
            infoPanel.getClockPanel().stop();
            soundManager.playGameOverSound();
            updateFullUI();
            showGameOverDialog();
        }
    }

    @Override
    public void onToggleFlip() {
        boardPanel.toggleFlip();
    }

    @Override
    public void onThemeChanged(ChessTheme theme) {
        boardPanel.setTheme(theme);
    }

    @Override
    public void onTimeModeChanged(int minutes, int increment) {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Changing time control will restart the game. Proceed?",
                "Change Time Control",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            infoPanel.getClockPanel().setTimeControls(minutes, increment);
            resetGame();
        }
    }

    @Override
    public void onTimeout(PieceColor timedOutColor) {
        if (gameState.isGameOver()) return;
        gameState = GameState.TIMEOUT;
        soundManager.playGameOverSound();
        updateFullUI();
        showGameOverDialog();
    }

    private void updateFullUI() {
        boardPanel.repaint();
        infoPanel.getMoveHistoryPanel().updateHistory(history);

        // Calculate captured pieces
        List<Piece> blackPiecesLost = history.getCapturedPieces(PieceColor.BLACK);
        List<Piece> whitePiecesLost = history.getCapturedPieces(PieceColor.WHITE);

        int whiteScore = board.getMaterialScore(PieceColor.WHITE);
        int blackScore = board.getMaterialScore(PieceColor.BLACK);

        int whiteAdvantage = Math.max(0, whiteScore - blackScore);
        int blackAdvantage = Math.max(0, blackScore - whiteScore);

        infoPanel.updateCapturedPieces(blackPiecesLost, whitePiecesLost, whiteAdvantage, blackAdvantage);

        boolean inCheck = board.isInCheck(board.getTurn());
        infoPanel.updateStatus(board.getTurn(), gameState, inCheck);
    }
}
