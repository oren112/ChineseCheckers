package chinesecheckers.ui;

import chinesecheckers.util.Player;
import chinesecheckers.util.Piece;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a square shaped board sub class of Board allowing 2 or 4 players.
 * Each player occupies a 10 positions corner for its initial pieces locations
 * and aims to move their pieces to their end zone which is the opposite corner.
 * @see Board
 */
public class SquareBoard extends Board {

    private static final int END_ZONE_LENGTH = 4;
    private static final int GRID_SIDE_LENGTH = 10;
    private static final int MAX_DISTANCE = 10;
    private static final int POSITIONS_MARGIN = 140;
    private static final int MINI_POSITIONS_MARGIN = 65;
    private static final int SPACE_BETWEEN_POSITIONS = 45;
    private static final int MINI_SPACE_BETWEEN_POSITIONS = 26;
    private static final int POSITION_RADIUS = 50;
    private static final int MINI_POSITION_RADIUS = 30;

    /**
     * Creates a square board allowing 2 or 4 players.
     * @param players the list of players
     * @param theme the theme defining the images of the board
     * @param mini if {@code true} creates a stateless mini board, otherwise creates a game board
     */
    public SquareBoard(ArrayList<Player> players, Theme theme, boolean mini) {
        super(MAX_DISTANCE, players, theme, mini);
    }

    @Override // creates initial positions and adds them to board
    public void addPositions(boolean mini) {
        virtualPositionToPositionMap = new HashMap<>();
        for (int row = 0; row < GRID_SIDE_LENGTH; row++) {
            for (int col = 0; col < GRID_SIDE_LENGTH; col++) {
                Piece piece = getInitialPiece(row, col);
                Position position = new Position(row, col, piece);
                if (mini) {
                    position.setBounds(MINI_SPACE_BETWEEN_POSITIONS * col + MINI_POSITIONS_MARGIN,
                            MINI_SPACE_BETWEEN_POSITIONS * row + MINI_POSITIONS_MARGIN,
                            MINI_POSITION_RADIUS, MINI_POSITION_RADIUS);
                    position.setIcon(theme.getMiniImageIcon(position.getPiece()));
                    position.setCursor(null);
                } else {
                    position.setBounds(SPACE_BETWEEN_POSITIONS * col + POSITIONS_MARGIN,
                            SPACE_BETWEEN_POSITIONS * row + POSITIONS_MARGIN,
                            POSITION_RADIUS, POSITION_RADIUS);
                    position.setIcon(theme.getImageIcon(position.getPiece()));
                    if (piece != Piece.NONE) {
                        state.addEndZonePosition(position.getVirtualPosition(), oppositePiece(piece)); // update board specific state
                    }
                    state.addPosition(position.getVirtualPosition(), position.getPiece());
                    position.addActionListener(this::selectPosition);
                }
                this.add(position);
                this.virtualPositionToPositionMap.put(position.getVirtualPosition(), position);
            }
        }
    }

    @Override // directions a piece can move
    int[][] getDirections() {
        return new int[][] {{0, 1}, {1, 0}, {1, 1}, {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}, {1, -1}};
    }

    // returns the piece that this location belongs to its initial positions
    // if not any initial position returns Piece.NONE
    Piece getInitialPiece(int row, int col) {
        if (getPlayers().size() == 0) return Piece.NONE;
        if (row + col < END_ZONE_LENGTH) return Piece.WHITE; // top left corner
        if (row + col > (MAX_DISTANCE - END_ZONE_LENGTH + 1) * 2) return Piece.BLACK; // bottom right corner
        if (this.getPlayers().size() == 4 && col - row >= MAX_DISTANCE - END_ZONE_LENGTH) return Piece.BLUE; // top right
        if (this.getPlayers().size() == 4 && row - col >= MAX_DISTANCE - END_ZONE_LENGTH) return Piece.YELLOW;  // bottom left
        return Piece.NONE;
    }

    // gets the piece of the opposite corner
    private Piece oppositePiece(Piece piece) {
        switch (piece) {
            case WHITE:
                return Piece.BLACK;
            case YELLOW:
                return Piece.BLUE;
            case BLACK:
                return Piece.WHITE;
            default: // BLUE
                return Piece.YELLOW;
        }
    }

}