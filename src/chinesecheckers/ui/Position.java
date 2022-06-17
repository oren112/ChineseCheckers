package chinesecheckers.ui;

import chinesecheckers.util.Piece;
import chinesecheckers.util.VirtualPosition;

import java.util.Objects;
import javax.swing.JButton;
import javax.swing.border.Border;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Graphics;

/**
 * UI component that represents a position on the board.
 */
public class Position extends JButton {

    private static final int RADIUS = 50;

    private final VirtualPosition virtualPosition;

    /**
     * Creates an object containing a board position {@link Piece}, location and UI.
     * @param row the row location
     * @param col the column location
     * @param piece the piece associated with this position
     */
    public Position(int row, int col, Piece piece) {
        this.virtualPosition = new VirtualPosition(row, col, piece);
        this.setBorder(new RoundedBorder(RADIUS));
        this.setOpaque(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Gets the piece associated with this position.
     * @return the piece associated with this position
     */
    public Piece getPiece() { return virtualPosition.getPiece(); }


    /**
     * Sets the piece associated with this position.
     * @param piece the piece to set
     */
    public void setPiece(Piece piece) {
        virtualPosition.setPiece(piece);
    }

    /**
     * Gets this virtual position
     * @return this virtual position
     * @see VirtualPosition
     */
    public VirtualPosition getVirtualPosition() {
        return virtualPosition;
    }

    @Override
    public String toString() {
        return "(" + virtualPosition.getRow() + "," + virtualPosition.getCol() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return virtualPosition.equals(position.virtualPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(virtualPosition);
    }

    private static class RoundedBorder implements Border {

        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

}
