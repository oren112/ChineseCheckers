package chinesecheckers.util;

import java.util.List;

/**
 * Represents a move from an origin virtual position to a destination virtual position.
 * A move is a single step to a neighbor position or a chain of hops, each over one piece.
 * @see VirtualPosition
 */
public class Move {

    private final VirtualPosition origin;
    private final VirtualPosition destination;
    private final Piece piece;
    private List<VirtualPosition> moveChain;

    /**
     * Creates an object representing a move originating at {@code origin}, arriving at {@code destination}
     * while passing through a list of positions specified at {@code moveChain}.
     * @param origin the origin position
     * @param destination the destination position
     * @param piece the piece that moves
     * @param moveChain the chain of moves which starts at {@code origin} and terminates at {@code destination}
     * @see VirtualPosition
     * @see Piece
     */
    public Move(VirtualPosition origin, VirtualPosition destination, Piece piece, List<VirtualPosition> moveChain) {
        this(origin, destination, piece);
        this.moveChain = moveChain;
    }


    /**
     * Creates an object representing a move originating at {@code origin} and arriving at {@code destination}
     * the path between the origin and destinations is omitted in this constructor.
     * @param origin the origin position
     * @param destination the destination position
     * @param piece the piece that moves
     * @see VirtualPosition
     * @see Piece
     */
    public Move(VirtualPosition origin, VirtualPosition destination, Piece piece) {
        this.origin = origin;
        this.destination = destination;
        this.piece = piece;
    }

    /**
     * Gets the move chain originating at this origin and terminating at this destination.
     * The move chain is a list of {@link VirtualPosition} objects starting at this origin
     * and terminating at this destination.
     * @return the move chain of this move
     */
    public List<VirtualPosition> getMoveChain() {
        return moveChain;
    }

    /**
     * Gets the {@link Piece} associated with this move.
     * @return the piece associated with this move
     */
    public Piece getPiece() { return this.piece; }

    /** Gets the origin {@link VirtualPosition} of this move.
     * @return the origin of this move
     */
    public VirtualPosition getOrigin() {
        return this.origin;
    }

    /** Gets the destination {@link VirtualPosition} of this move.
     * @return the destination of this move
     */
    public VirtualPosition getDestination() {
        return this.destination;
    }

    @Override
    public String toString() {
        return this.getOrigin() + " -> " + this.getDestination();
    }
}
