package chinesecheckers.util;
import java.util.Objects;

/**
 * Represents two dimensional coordinates (row, column)
 */
public class Coordinates {

    private final int row;
    private final int col;

    /**
     * Creates a two dimensional coordinates object.
     * @param row the row coordinate
     * @param col the column coordinate
     */
    public Coordinates(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row of this coordinates.
     * @return the row of this coordinates
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column of this coordinates.
     * @return the column of this coordinates
     */
    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates coordinates = (Coordinates) o;
        return row == coordinates.row &&
                col == coordinates.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
