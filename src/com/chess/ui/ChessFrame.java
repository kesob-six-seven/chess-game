package com.chess.ui;

import com.chess.controller.GameController;
import com.chess.model.Piece;
import com.chess.model.PieceColor;
import com.chess.model.PieceType;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Main application window for the 1v1 Local Chess game.
 */
public class ChessFrame extends JFrame {
    private final GameController controller;
    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel;

    public ChessFrame() {
        super("Java Chess - Local 1v1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(840, 680));
        setPreferredSize(new Dimension(980, 750));

        // Generate procedural window icon
        setIconImage(createAppIcon());

        controller = new GameController();
        boardPanel = new BoardPanel(controller.getBoard());
        infoPanel = new InfoPanel();

        controller.setViews(this, boardPanel, infoPanel);

        // Build Menu Bar
        setJMenuBar(createMenuBar());

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(22, 23, 26));
        mainContent.add(boardPanel, BorderLayout.CENTER);
        mainContent.add(infoPanel, BorderLayout.EAST);

        setContentPane(mainContent);
        pack();
        setLocationRelativeTo(null);
    }

    private BufferedImage createAppIcon() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(45, 52, 65));
        g.fillRoundRect(0, 0, size, size, 16, 16);
        Piece knight = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        PiecePainter.drawPiece(g, knight, 6, 6, 52);
        g.dispose();
        return img;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(32, 34, 38));
        menuBar.setBorder(null);

        // --- Game Menu ---
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setForeground(Color.WHITE);

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newGameItem.addActionListener(e -> controller.onNewGame());

        JMenuItem undoItem = new JMenuItem("Undo Move");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> controller.onUndo());

        JMenuItem offerDrawItem = new JMenuItem("Offer Draw");
        offerDrawItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
        offerDrawItem.addActionListener(e -> controller.onOfferDraw());

        JMenuItem resignItem = new JMenuItem("Resign");
        resignItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        resignItem.addActionListener(e -> controller.onResign());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.add(undoItem);
        gameMenu.addSeparator();
        gameMenu.add(offerDrawItem);
        gameMenu.add(resignItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        // --- View Menu ---
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);

        JMenuItem flipItem = new JMenuItem("Flip Board");
        flipItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        flipItem.addActionListener(e -> controller.onToggleFlip());

        JCheckBoxMenuItem autoFlipItem = new JCheckBoxMenuItem("Auto-Flip on Turn Change");
        autoFlipItem.setSelected(controller.isAutoFlipBoard());
        autoFlipItem.addActionListener(e -> controller.setAutoFlipBoard(autoFlipItem.isSelected()));

        JMenu themeSubMenu = new JMenu("Board Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        for (ChessTheme theme : ChessTheme.values()) {
            JRadioButtonMenuItem tItem = new JRadioButtonMenuItem(theme.getDisplayName());
            if (theme == boardPanel.getTheme()) tItem.setSelected(true);
            tItem.addActionListener(e -> {
                controller.onThemeChanged(theme);
            });
            themeGroup.add(tItem);
            themeSubMenu.add(tItem);
        }

        viewMenu.add(flipItem);
        viewMenu.add(autoFlipItem);
        viewMenu.addSeparator();
        viewMenu.add(themeSubMenu);

        // --- Audio Menu ---
        JMenu audioMenu = new JMenu("Audio");
        audioMenu.setForeground(Color.WHITE);

        JCheckBoxMenuItem soundItem = new JCheckBoxMenuItem("Sound Effects", true);
        soundItem.addActionListener(e -> controller.getSoundManager().setSoundEnabled(soundItem.isSelected()));
        audioMenu.add(soundItem);

        // --- Help Menu ---
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);

        JMenuItem rulesItem = new JMenuItem("Chess Rules & Controls");
        rulesItem.addActionListener(e -> showRulesDialog());

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(rulesItem);
        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        menuBar.add(audioMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showRulesDialog() {
        String msg = "<html><body style='width: 340px; padding: 5px;'>" +
                "<h3>Chess Controls:</h3>" +
                "<ul>" +
                "<li><b>Move:</b> Click a piece to select it and click any highlighted destination, OR drag-and-drop piece directly.</li>" +
                "<li><b>Deselect:</b> Click anywhere else or right-click.</li>" +
                "<li><b>Special Moves:</b> Castling, En Passant, and Pawn Promotion are fully supported with official FIDE rules.</li>" +
                "<li><b>Shortcuts:</b> Ctrl+N (New Game), Ctrl+Z (Undo), Ctrl+F (Flip Board).</li>" +
                "</ul>" +
                "<h3>Rules Enforced:</h3>" +
                "<ul>" +
                "<li>Check and Checkmate detection.</li>" +
                "<li>Pins and restrictions preventing king-exposure.</li>" +
                "<li>Stalemate and Insufficient Material draws.</li>" +
                "<li>50-move rule & Threefold Repetition tracking.</li>" +
                "</ul>" +
                "</body></html>";
        JOptionPane.showMessageDialog(this, msg, "Rules & Controls", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String msg = "<html><body style='text-align: center; width: 280px; padding: 10px;'>" +
                "<h2>Java Chess 1v1</h2>" +
                "<p>A complete 2-player local chess experience built purely with modern Java & Swing.</p>" +
                "<p>Custom vector piece graphics, procedural tactical audio, clock timers, move logs, and full chess rules.</p>" +
                "<p><small>Enjoy playing with your friend!</small></p>" +
                "</body></html>";
        JOptionPane.showMessageDialog(this, msg, "About Java Chess", JOptionPane.INFORMATION_MESSAGE);
    }
}
