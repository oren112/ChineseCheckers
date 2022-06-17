package chinesecheckers.util;

import java.util.Objects;

/**
 * Represents a position on the board.
 */
public class VirtualPosition {

    private final int row;
    private final int col;
    private Piece piece;

    /**
     * Creates a virtual board position
     * @param row the row location
     * @param col the column location
     * @param piece the piece associated with this position
     */
    public VirtualPosition(int row, int col, Piece piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    /**
     * Gets the piece associated with this position.
     * @return the piece associated with this position.
     */
    public Piece getPiece() { return piece; }

    /**
     * Gets the row location of this position.
     * @return the row location of this position
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column location of this position.
     * @return the column location of this position
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the piece associated with this position.
     * @param piece the piece to set
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualPosition that = (VirtualPosition) o;
        return row == that.row &&
                col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
