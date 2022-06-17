package chinesecheckers.ui;

import chinesecheckers.util.Player;
import chinesecheckers.util.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a 6 corner star shaped board sub class of Board allowing 2, 4 or 6 players.
 * The star shape has 6 equilateral triangles of side length 4 as corners designated for initial positions,
 * and a regular hexagon middle of side length 5.
 * Each player occupies a 10 positions triangle corner for its initial pieces locations
 * and aims to move their pieces to their end zone which is the opposite triangle corner.
 * @see Board
 */
public class StarBoard extends Board{

    private final static int MAX_DISTANCE = 17; // max number of steps from between two positions.
    private final static int HORIZONTAL_MAX_DISTANCE = 13; // max number of horizontal steps between two positions.
    private final static int END_ZONE_LENGTH = 4; // end zone front line length.

    private static final int HORIZONTAL_POSITIONS_MARGIN = 65;
    private static final int VERTICAL_POSITIONS_MARGIN = 25;
    private static final int MINI_HORIZONTAL_POSITIONS_MARGIN = 40;
    private static final int MINI_VERTICAL_POSITIONS_MARGIN = 25;
    private static final int VERTICAL_SPACE_BETWEEN_POSITIONS = 20;
    private static final int MINI_VERTICAL_SPACE_BETWEEN_POSITIONS = 10;
    private static final int HORIZONTAL_SPACE_BETWEEN_POSITIONS = 23;
    private static final int MINI_HORIZONTAL_SPACE_BETWEEN_POSITIONS = 12;
    private static final int POSITION_RADIUS = 50;
    private static final int MINI_POSITION_RADIUS = 30;

    /** Creates a star shaped board for 2, 4 or 6 players.
     * @param players the list of players
     * @param theme the theme defining the images of the board
     * @param mini if {@code true} creates a stateless mini board, otherwise creates a game board
     */
    public StarBoard(ArrayList<Player> players, Theme theme, boolean mini) {
        super(MAX_DISTANCE, players, theme, mini);
    }
    // adds a single initial position to the state and the UI component
    private void addPosition(Integer[] location, Piece piece, boolean mini) {
        Position position = new Position(location[0], location[1], piece);
        if (mini) {
            position.setCursor(null);
            position.setBounds(MINI_HORIZONTAL_SPACE_BETWEEN_POSITIONS * location[1] + MINI_HORIZONTAL_POSITIONS_MARGIN,
                    MINI_VERTICAL_SPACE_BETWEEN_POSITIONS * location[0] + MINI_VERTICAL_POSITIONS_MARGIN,
                    MINI_POSITION_RADIUS, MINI_POSITION_RADIUS);
            position.setIcon(theme.getMiniImageIcon(piece));
        } else {
            position.setBounds(HORIZONTAL_SPACE_BETWEEN_POSITIONS * location[1] + HORIZONTAL_POSITIONS_MARGIN,
                    VERTICAL_SPACE_BETWEEN_POSITIONS * location[0] + VERTICAL_POSITIONS_MARGIN,
                    POSITION_RADIUS, POSITION_RADIUS);

            state.addPosition(position.getVirtualPosition(), piece);
            if (piece != Piece.NONE) {
                state.addEndZonePosition(position.getVirtualPosition(), oppositePiece(piece));
            }
            position.addActionListener(this::selectPosition);
            position.setIcon(theme.getImageIcon(piece));
        }
        this.add(position);
        virtualPositionToPositionMap.put(position.getVirtualPosition(), position);
    }

    @Override // adds all initial positions to the state and to the board UI component
    void addPositions(boolean mini) {
        virtualPositionToPositionMap = new HashMap<>();
        Piece yellowLocationPiece = players.size() > 2 ? Piece.YELLOW : Piece.NONE;
        Piece blueLocationPiece = players.size() > 2 ? Piece.BLUE : Piece.NONE;
        Piece greenLocationPiece = players.size() > 4 ? Piece.GREEN : Piece.NONE;
        Piece redLocationPiece = players.size() > 4 ? Piece.RED : Piece.NONE;

        Integer[] blackInitialLocation = {4 * END_ZONE_LENGTH - 2, 2 * HORIZONTAL_MAX_DISTANCE - END_ZONE_LENGTH - 1};
        new EquilateralTriangleIterator(blackInitialLocation, END_ZONE_LENGTH, true)
                .forEachRemaining(location -> addPosition(location, Piece.BLACK, mini));

        Integer[] whiteInitialLocation = {4 * END_ZONE_LENGTH + 2, END_ZONE_LENGTH - 1};
        new EquilateralTriangleIterator(whiteInitialLocation, END_ZONE_LENGTH, false)
                .forEachRemaining(location -> addPosition(location, Piece.WHITE, mini));

        Integer[] yellowInitialLocation = {4 * END_ZONE_LENGTH - 2, END_ZONE_LENGTH - 1};
        new EquilateralTriangleIterator(yellowInitialLocation, END_ZONE_LENGTH, true)
                .forEachRemaining(location -> addPosition(location, yellowLocationPiece, mini));

        Integer[] blueInitialLocation = {4 * END_ZONE_LENGTH + 2, 2 * HORIZONTAL_MAX_DISTANCE - END_ZONE_LENGTH - 1};
        new EquilateralTriangleIterator(blueInitialLocation, END_ZONE_LENGTH, false)
                .forEachRemaining(location -> addPosition(location, blueLocationPiece, mini));

        Integer[] greenInitialLocation = {8 * END_ZONE_LENGTH, HORIZONTAL_MAX_DISTANCE - 1};
        new EquilateralTriangleIterator(greenInitialLocation, END_ZONE_LENGTH, true)
                .forEachRemaining(location -> addPosition(location, greenLocationPiece, mini));

        Integer[] redInitialLocation = {0, HORIZONTAL_MAX_DISTANCE - 1};
        new EquilateralTriangleIterator(redInitialLocation, END_ZONE_LENGTH, false)
                .forEachRemaining(location -> addPosition(location, redLocationPiece, mini));

        Integer[] emptyHexagonInitialLocation = {2 * END_ZONE_LENGTH, 2 * END_ZONE_LENGTH};
        new RegularHexagonIterator(emptyHexagonInitialLocation, HORIZONTAL_MAX_DISTANCE - 2 * END_ZONE_LENGTH)
                .forEachRemaining(location -> addPosition(location, Piece.NONE, mini));
    }

    @Override
    int[][] getDirections() {
        return new int[][] {{-2, -1}, {-2, 1}, {0, -2}, {0, 2}, {2, -1}, {2, 1}};
    }

    // Iterates an equilateral triangle using the directions specified in getDirections()
    private static class EquilateralTriangleIterator implements Iterator<Integer[]> {

        private Integer[] location = null;
        private final Integer[] peakLocation;
        private final int size;
        private final int sign;
        private int left;
        private int right;

        // the peak location is either bottom, if upsideDown is set to true, or top otherwise.
        private EquilateralTriangleIterator(Integer[] peakLocation, int size, boolean upsideDown) {
            this.peakLocation = peakLocation;
            this.size = size;
            this.sign = upsideDown ? -1 : 1;
            this.left = peakLocation[1];
            this.right = peakLocation[1];
        }

        @Override
        public boolean hasNext() {
            if (location == null) {
                return true;
            }
            return !location[1].equals(peakLocation[1] + size - 1);
        }

        @Override
        public Integer[] next() {
            if (location == null) { // first location
                location = new Integer[] {peakLocation[0], peakLocation[1]};
                return location;
            }
            if (location[1].equals(right)) { // new line
                right++;
                left--;
                location[0] += sign * 2;
                location[1] = left;
                return location;
            }
            location[1] += 2;
            return location;
        }
    }

    // Iterates a regular hexagon using the directions specified in getDirections()
    private static class RegularHexagonIterator implements Iterator<Integer[]> {

        private Integer[] location = null;
        private final Integer[] topLeftLocation;
        private final int size;
        private int right;
        private int left;

        private RegularHexagonIterator(Integer[] topLeftLocation, int size) {
            this.topLeftLocation = topLeftLocation;
            this.size = size;
            this.right = 2 * (size - 1) + topLeftLocation[1];
            this.left = topLeftLocation[1];
        }

        @Override
        public boolean hasNext() {
            if (location == null) {
                return true;
            }
            return !(location[0].equals(topLeftLocation[0] + 4 * (size - 1))
                    && location[1].equals(topLeftLocation[1] + 2 * (size - 1)));
        }

        @Override
        public Integer[] next() {
            if (location == null) { // first location
                location = new Integer[] {topLeftLocation[0], topLeftLocation[1]};
                return location;
            }
            if (location[1].equals(right)) { // new line
                if (location[0] < 4 * (size - 1)) {
                    left--;
                    right++;
                } else {
                    left++;
                    right--;
                }
                location[0] += 2;
                location[1] = left;
                return location;
            }
            location[1] += 2;
            return location;
        }
    }

    // gets the piece in the opposite corner
    private Piece oppositePiece(Piece piece) {
        switch (piece) {
            case WHITE:
                return Piece.BLACK;
            case YELLOW:
                return Piece.BLUE;
            case RED:
                return Piece.GREEN;
            case BLACK:
                return Piece.WHITE;
            case BLUE:
                return Piece.YELLOW;
            default: // GREEN
                return Piece.RED;
        }
    }
}
