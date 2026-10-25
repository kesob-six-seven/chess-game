package com.chess.ui;

import com.chess.model.Move;
import com.chess.model.MoveHistory;
import com.chess.model.PieceColor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

/**
 * Visual move history table displaying standard algebraic notation for White and Black moves.
 */
public class MoveHistoryPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JScrollPane scrollPane;

    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columns = {"#", "White", "Black"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(36, 38, 43));
        table.setForeground(new Color(230, 230, 235));
        table.setGridColor(new Color(50, 52, 60));
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(28, 30, 34));
        table.getTableHeader().setForeground(new Color(170, 175, 185));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (col == 0) {
                    setForeground(new Color(140, 145, 155));
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setHorizontalAlignment(CENTER);
                } else {
                    setForeground(new Color(235, 238, 245));
                    setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
                    setHorizontalAlignment(LEFT);
                }
                setBackground(row % 2 == 0 ? new Color(36, 38, 43) : new Color(32, 34, 38));
                return c;
            }
        };

        for (int i = 0; i < 3; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 58, 66), 1));
        scrollPane.getViewport().setBackground(new Color(36, 38, 43));
        scrollPane.setPreferredSize(new Dimension(240, 260));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateHistory(MoveHistory history) {
        tableModel.setRowCount(0);
        List<MoveHistory.HistoryEntry> entries = history.getEntries();

        int moveNumber = 1;
        for (int i = 0; i < entries.size(); i += 2) {
            String whiteSan = entries.get(i).getMove().getSanNotation();
            String blackSan = (i + 1 < entries.size()) ? entries.get(i + 1).getMove().getSanNotation() : "";
            tableModel.addRow(new Object[]{moveNumber + ".", whiteSan, blackSan});
            moveNumber++;
        }

        // Auto-scroll to bottom
        if (table.getRowCount() > 0) {
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        }
    }

    public void clear() {
        tableModel.setRowCount(0);
    }
}
