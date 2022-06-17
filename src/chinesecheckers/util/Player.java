package chinesecheckers.util;

import chinesecheckers.ai.Mode;

/**
 * Represents a human or AI player.
 */
public class Player {

    private final Piece piece;
    private boolean human;
    private Mode mode;

    /**
     * Creates an object representing a human or AI player with its associated {@link Piece}.
     * @param piece the piece associated with the player
     * @param human true for human, false for AI
     */
    public Player(Piece piece, boolean human) {
        this.piece = piece;
        this.human = human;
    }

    /**
     * Gets the {@link Mode} of this player. relevant only for non human players.
     * @return the mode associated with this player
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the {@link Mode} of this player. relevant only for non human players.
     * @param mode the mode to be set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Sets the type of this player to human or to not human.
     * @param human the type of player to be set
     */
    public void setHuman(boolean human) {
        this.human = human;
    }

    /**
     * Gets the {@link Piece} associated with this player.
     * @return the piece associated with this player
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Checks if the player type is AI.
     * @return {@code true} if {@code human} is set to {@code false}, {@code false} otherwise
     */
    public boolean isAI() {
        return !human;
    }

    @Override
    public String toString() {
        if (this.getPiece() == Piece.WHITE) return "white";
        if (this.getPiece() == Piece.BLACK) return "black";
        if (this.getPiece() == Piece.YELLOW) return "yellow";
        if (this.getPiece() == Piece.BLUE) return "blue";
        if (this.getPiece() == Piece.GREEN) return "green";
        return "red";
    }



}
