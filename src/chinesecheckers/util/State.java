package chinesecheckers.util;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * Keeps track of the state of the game.
 */
public class State {

    private final Map<Piece, Set<VirtualPosition>> piecePositionsMap; // maps each piece to the set of its positions
    private final Map<Player, Integer> playerDistanceMap; // maps each player to the distance it needs to cover to win
    private final Map<Piece, Set<VirtualPosition>> pieceEndZonePositionsMap; // maps each piece to the set of its end zone positions
    private final Map<Piece, Player> piecePlayerMap; // maps each piece to its player
    private final Map<Coordinates, VirtualPosition> pointPositionMap; // maps each point (coordinates) to its position
    private final Map<Player, VirtualPosition> playerFarthestPositionMap; // maps each player to its farthest position
    private VirtualPosition center; // the center of the board

    private PositionTreeNode destinationTree; // a tree rooted at an origin position with destinations as other nodes
    private final Set<PositionTreeNode> destinations; // a set of pointers to the destinations tree node excluding the root

    private final ArrayList<Player> players; // the players of this game
    private Player currentPlayer; // keep track of the player which it is its turn
    private Player winner; // keep track of the winner of the game

    private final int maxDistance; // the max distance between a position and an end zone position
    private final int[][] directions; // the directions in which a piece can move

    /**
     * Creates an uninitialized state of a game.
     * @param players the players of the game
     * @param maxDistance the max distance between a position and an end zone position
     * @param directions the directions in which a piece can move
     */
    public State(ArrayList<Player> players, int maxDistance, int[][] directions) {
        this.directions = directions;
        this.maxDistance = maxDistance;
        this.players = players;
        this.currentPlayer = players.get(0);
        this.playerDistanceMap = new HashMap<>();
        this.piecePositionsMap = new HashMap<>();
        this.pieceEndZonePositionsMap = new HashMap<>();
        this.piecePlayerMap = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerFarthestPositionMap = new HashMap<>();
        this.pointPositionMap = new HashMap<>();
        this.piecePositionsMap.put(Piece.NONE, new HashSet<>());
        for (Player player : players) {
            this.playerDistanceMap.put(player, 0);
            this.piecePositionsMap.put(player.getPiece(), new HashSet<>());
            this.pieceEndZonePositionsMap.put(player.getPiece(), new HashSet<>());
            this.piecePlayerMap.put(player.getPiece(), player);
        }
    }

    /**
     * Creates a copy of the specified state.
     * @param other a state to copy
     */
    public State(State other) {
        this.winner = other.winner;
        this.directions = other.directions;
        this.maxDistance = other.maxDistance;
        this.players = other.getPlayers();
        this.currentPlayer = other.getCurrentPlayer();
        this.playerDistanceMap = new HashMap<>();
        this.piecePositionsMap = new HashMap<>();
        this.pieceEndZonePositionsMap = new HashMap<>();
        this.piecePlayerMap = new HashMap<>();
        this.destinations = new HashSet<>();
        this.playerFarthestPositionMap = new HashMap<>();
        this.destinationTree = null;
        this.pointPositionMap = new HashMap<>(other.pointPositionMap);
        this.piecePositionsMap.put(Piece.NONE, other.getPositions(Piece.NONE));
        for (Player player : this.players) {
            this.playerDistanceMap.put(player, other.playerDistanceMap.get(player));
            this.piecePositionsMap.put(player.getPiece(), new HashSet<>(other.piecePositionsMap.get(player.getPiece())));
            this.pieceEndZonePositionsMap.put(player.getPiece(), other.getEndZonePositions(player.getPiece()));
            this.piecePlayerMap.put(player.getPiece(), player);
            this.playerFarthestPositionMap.put(player, other.playerFarthestPositionMap.get(player));
        }
        this.center = other.center;
    }

    /**
     * Creates a state with the copy constructor and applies a {@link Move} to the copied state.
     * @param state the state to copy
     * @param moveToApply the move to apply on the copied state
     */
    public State(State state, Move moveToApply) {
        this(state);
        movePiece(moveToApply);
    }

    /**
     * Gets the farthest position of the specified {@link Player} calculated by {@link #updateFarthestPositions()}.
     * @param player the player to get its farthest position
     * @return the farthest position of the specified player
     */
    public VirtualPosition getFarthestPosition(Player player) {
        return playerFarthestPositionMap.get(player);
    }

    /**
     * Gets the center calculated by {@link #updateCenter()}.
     * @return the center of the board
     */
    public VirtualPosition getCenter() {
        return center;
    }

    /**
     * Gets the number of pieces each player has.
     * @return the number of pieces
     */
    public int getNumPieces() {
        return piecePositionsMap.get(players.get(0).getPiece()).size();
    }

    /**
     * Gets the max distance between a position and an end zone position.
     * @return the max distance
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Gets the set of end zone positions of the {@link Piece}.
     * @param piece the piece to return its end zone positions
     * @return the end zone positions of {@code piece}
     */
    public Set<VirtualPosition> getEndZonePositions(Piece piece) {
        return pieceEndZonePositionsMap.get(piece);
    }

    /**
     * Gets the list of players.
     * @return the list of players
     * @see Player
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the map that maps a {@link Piece} to its {@link Player}.
     * @return the map from pieces to players
     */
    public Map<Piece, Player> getPiecePlayerMap() {
        return piecePlayerMap;
    }

    /**
     * Gets the winner of this game.
     * @return the winner of this game if exists, otherwise {@code null}
     * @see Player
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Gets the {@code player} sum of distances from its positions to its end zone,
     * where distance is calculated by {@link #distance(VirtualPosition, VirtualPosition)}
     * @param player the player to calculate distance for
     * @return the player's distance
     * @see Player
     * @see VirtualPosition
     */
    public int getPlayerDistance(Player player) {
        return playerDistanceMap.get(player);
    }

    /**
     * Gets the player which is to play next.
     * @return the current player.
     * @see Player
     */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Gets the positions in the board containing {@code piece}.
     * @param piece the piece to get positions for.
     * @return the set of positions.
     * @see Piece
     * @see VirtualPosition
     */
    public Set<VirtualPosition> getPositions(Piece piece) {
        return new HashSet<>(piecePositionsMap.get(piece));
    }

    /** Sets the winner of this game to the first {@link Player} that returns {@code true} from {@link #isWinner(Player)}
     * @param winner the winner
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Calculates and updates the position which its coordinates are farthest from some piece of a {@link Player}.
     * This is calculated for every player.
     * pre: should be calculated once, after initializing all positions, and before any move has been applied.
     */
    public void updateFarthestPositions() {
        for (Player player : players) {
            int maxDistance = Integer.MIN_VALUE;
            for (VirtualPosition position : piecePositionsMap.get(player.getPiece())) {
                for (VirtualPosition endZonePosition : pieceEndZonePositionsMap.get(player.getPiece())) {
                    int distance = distance(position, endZonePosition);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        playerFarthestPositionMap.put(player, endZonePosition);
                    }
                }
            }
        }
    }

    /**
     * Calculates the number of positions in the end zone of {@code piece} that are not containing {@code currentPiece}.
     * @param piece the piece to check its end zone
     * @param currentPiece the piece to not count its occurrences
     * @return the number of positions
     */
    public int getNumOtherPiecesInEndZone(Piece piece, Piece currentPiece) {
        return (int) pieceEndZonePositionsMap.get(piece)
                .stream()
                .filter(position -> position.getPiece() != Piece.NONE && position.getPiece() != currentPiece)
                .count();
    }

    /**
     * Takes a destination and calculates a path to the origin position.
     * Destinations are leaves in the tree data structure this {@code destinations}
     * and the root is the origin.
     * @param destination the {@link VirtualPosition} to calculate the path for
     * @return the path calculated
     */
    public List<VirtualPosition> getMoveChain(VirtualPosition destination) {
        List<VirtualPosition> moveChain = new ArrayList<>();
        for (PositionTreeNode destinationNode : destinations) {
            if (destinationNode.position == destination) {
                while (destinationNode != null) {
                    moveChain.add(0, destinationNode.position);
                    destinationNode = destinationNode.parent;
                }
            }
        }
        return moveChain;
    }

    /**
     * Clears the destinations, to be used before calculating new destinations or after extracting all needed data.
     * @see PositionTreeNode
     */
    public void clearDestinations() {
        destinationTree = null;
        destinations.clear();
    }

    /**
     * Checks whether a position is in the destinations data structure.
     * @param position the position to check
     * @return {@code true} if the position is a destination, {@code false} otherwise
     * @see VirtualPosition
     * @see PositionTreeNode
     */
    public boolean isDestination(VirtualPosition position) {
        return destinations.contains(new PositionTreeNode(position));
    }

    /**
     * Checks whether the {@link Player} has at least one piece in its end zone and that its end zone
     * doesn't contain empty positions.
     * @param player the player to check win for
     * @return {@code true} if the player won, {@code false} otherwise
     */
    public boolean isWinner(Player player) {
        boolean hasPieceInEndZone = false;
        for (VirtualPosition endZonePosition : getEndZonePositions(player.getPiece())) {
            if (endZonePosition.getPiece() == Piece.NONE) {
                return false;
            } else if (endZonePosition.getPiece() == player.getPiece()) {
                hasPieceInEndZone = true;
            }
        }
        return hasPieceInEndZone;
    }

    /**
     * Changes the position's owner and updates the player distance accordingly.
     * for example when moving a piece or when initializing.
     * @param position the position to change its owner to {@code piece}
     * @param piece the new owner of the position
     */
    public void addPosition(VirtualPosition position, Piece piece) {
        this.piecePositionsMap.get(piece).add(position);
        if (piece != Piece.NONE) {
            Player player = piecePlayerMap.get(piece);
            playerDistanceMap.put(player, playerDistanceMap.get(player) + distanceToFreeEndZone(piece, position));
        }
    }

    /**
     * Changes the position's owner and updates the player distance accordingly.
     * for example when moving a piece.
     * @param position the position to change its owner from {@code piece}
     * @param piece the previous owner of the position
     */
    public void removePosition(VirtualPosition position, Piece piece) {
        piecePositionsMap.get(piece).remove(position);
        if (piece != Piece.NONE) {
            Player player = piecePlayerMap.get(piece);
            playerDistanceMap.put(player, playerDistanceMap.get(player) - distanceToFreeEndZone(piece, position));
        }
    }

    /**
     * Updates this {@code piecePositionMap} according to the specified move.
     * @param move the move to apply
     */
    public void movePiece(Move move) {
        Player player = piecePlayerMap.get(move.getPiece());
        this.piecePositionsMap.get(Piece.NONE).remove(move.getDestination());
        this.piecePositionsMap.get(Piece.NONE).add(move.getOrigin());
        removePosition(move.getOrigin(), move.getPiece());
        addPosition(move.getDestination(), move.getPiece());
        clearDestinations();
        if (isWinner(player)) {
            playerDistanceMap.put(player, 0);
            winner = player;
        }
    }

    /**
     * Updates a map from coordinates to virtual positions.
     * @see Coordinates
     * @see VirtualPosition
     */
    public void updatePointPositionMap() {
        for (Piece piece : piecePositionsMap.keySet()) {
            for (VirtualPosition position : piecePositionsMap.get(piece)) {
                pointPositionMap.put(new Coordinates(position.getRow(), position.getCol()), position);
            }
        }
    }

    /**
     * Calculates and updates the position which its coordinates are closest to the average of all empty positions.
     * pre: should be calculated once, after initializing all positions, and before any move has been applied.
     */
    public void updateCenter() {
        double rowSum = 0;
        double colSum = 0;
        for (VirtualPosition position : piecePositionsMap.get(Piece.NONE)) {
            rowSum += position.getRow();
            colSum += position.getCol();
        }
        double rowAverage = rowSum / piecePositionsMap.get(Piece.NONE).size();
        double colAverage = colSum / piecePositionsMap.get(Piece.NONE).size();
        VirtualPosition center = null;
        double distance = Integer.MAX_VALUE;
        for (VirtualPosition position : piecePositionsMap.get(Piece.NONE)) {
            double distanceFromCenter = Math.abs(position.getRow() - rowAverage)
                    + Math.abs(position.getCol() - colAverage);
            if (distanceFromCenter < distance) {
                center = position;
                distance = distanceFromCenter;
            }
        }
        this.center = center;
    }

    /**
     * Adds a single position to the map holding the end zone positions by piece.
     * This method is called when initializing the state by {@code Board}.
     * @param position the position to be added
     * @param endZonePiece the piece which {@code position} belongs to its end zone
     * @see VirtualPosition
     * @see Piece
     */
    public void addEndZonePosition(VirtualPosition position, Piece endZonePiece) {
        pieceEndZonePositionsMap.get(endZonePiece).add(position);
    }

    /**
     * Calculates and the distance between two {@code Position} objects.
     * @param position1 first position.
     * @param position2 second position.
     * @return the distance between the positions.
     * @see VirtualPosition
     */
    public int distance(VirtualPosition position1, VirtualPosition position2) {
        int row = position1.getRow();
        int col = position1.getCol();
        int nextRow = row;
        int nextCol = col;
        int stepCounter = 0;
        while (row != position2.getRow() || col != position2.getCol()) {
            int minDistance = Integer.MAX_VALUE;
            for (int[] direction : directions) {
                int distance = Math.abs(position2.getRow() - (row + direction[0]))
                        + Math.abs(position2.getCol() - (col + direction[1]));
                if (distance < minDistance) {
                    minDistance = distance;
                    nextRow = row + direction[0];
                    nextCol = col + direction[1];
                }
            }
            row = nextRow;
            col = nextCol;
            stepCounter++;
        }
        return stepCounter;
    }

    /**
     * Calculates the number of steps from {@code origin} to the closest end zone position of {@code piece},
     * where a step is a move to a free or occupied neighbor position.
     * @param piece the piece to calculate the distance to its end zone
     * @param origin the position to calculate the distance from
     * @return the distance to the closest end zone position of {@code piece}
     * @see VirtualPosition
     */
    public int distanceToFreeEndZone(Piece piece, VirtualPosition origin) {
        if (pieceEndZonePositionsMap.get(piece).contains(origin)) {
            return 0;
        }
        int distanceToFreeEndZone = Integer.MAX_VALUE;
        for (VirtualPosition endZonePosition : getEndZonePositions(piece)) {
            if (endZonePosition.getPiece() != piece) {
                int distance = distance(origin, endZonePosition);
                if (distance < distanceToFreeEndZone) {
                    distanceToFreeEndZone = distance;
                }
            }
        }
        return distanceToFreeEndZone;
    }

    /**
     * @return the next {@link Player} to make a move.
     */
    public Player nextTurn() {
        return players.get((players.indexOf(currentPlayer) + 1) % players.size());
    }

    /**
     * Changes the turn to the next {@link Player}.
     */
    public void changeTurn() {
        currentPlayer = nextTurn();
    }

    // checks if current position has a piece in it
    private boolean isOccupied(VirtualPosition position) {
        for (Player player : players) {
            if (piecePositionsMap.get(player.getPiece()).contains(position)) {
                return true;
            }
        }
        return false;
    }

    // checks if current position hasn't got a piece in it
    private boolean isFree(VirtualPosition position) {
        return piecePositionsMap.get(Piece.NONE).contains(position);
    }

    // adds all legal hop destinations to the destinations data structure
    private void updateHopDestinations(VirtualPosition origin) {
        updateHopDestinationsRecursive(origin, destinations, destinationTree);
    }

    // helper for updateHopDestinations method
    private void updateHopDestinationsRecursive(VirtualPosition origin, Set<PositionTreeNode> destinations, PositionTreeNode parent) {
        PositionTreeNode child = new PositionTreeNode(origin, parent);
        if (!destinations.contains(child)) { // not visiting same node twice
            destinations.add(child);
            if (parent != null) {
                parent.addChild(child);
            }
            // takes all single hop destinations and continues recursively
            Set<VirtualPosition> singleHopDestinations = getSingleHopDestinations(origin);
            for (VirtualPosition destination : singleHopDestinations) {
                updateHopDestinationsRecursive(destination, destinations, child);
            }
        }
    }

    // calculates and returns a set of single hop destination originating at the origin parameter
    private Set<VirtualPosition> getSingleHopDestinations(VirtualPosition origin) {
        Set<VirtualPosition> destinations = new HashSet<>();
        for (int[] direction : directions) {
            Coordinates neighborLocation =
                    new Coordinates(origin.getRow() + direction[0], origin.getCol() + direction[1]);
            Coordinates nextNeighborLocation =
                    new Coordinates(origin.getRow() + 2*direction[0], origin.getCol() + 2*direction[1]);
            VirtualPosition neighbor = pointPositionMap.get(neighborLocation);
            VirtualPosition nextNeighbor = pointPositionMap.get(nextNeighborLocation);
            if (isOccupied(neighbor) && isFree(nextNeighbor)) {
                destinations.add(nextNeighbor);
            }
        }
        return destinations;
    }

    // adds legal neighbor destinations to the destinations data structure
    private void updateNeighborDestinations(VirtualPosition origin) {
        for (int[] direction : directions) { // a neighbor is an empty position one step away from the origin
            VirtualPosition neighbor =
                    pointPositionMap.get(new Coordinates(origin.getRow() + direction[0], origin.getCol() + direction[1]));
            if (isFree(neighbor)) {
                PositionTreeNode positionTreeNode = new PositionTreeNode(neighbor);
                destinations.add(positionTreeNode);
                if (destinationTree == null) {
                    destinationTree = new PositionTreeNode(origin);
                }
                destinationTree.addChild(positionTreeNode);
                positionTreeNode.parent = destinationTree;
            }
        }
    }

    /**
     * Updates {@code this.destinations} to hold the set of destinations of legal moves
     * that originate at {@code origin}.
     *
     * @param origin a position to update legal destinations for
     * @see VirtualPosition
     * @see PositionTreeNode
     */
    public void updateDestinations(VirtualPosition origin) {
        updateHopDestinations(origin);
        updateNeighborDestinations(origin);
        destinations.remove(new PositionTreeNode(origin));
    }

    /**
     * Extracts positions from last calculated destinations and returns them.
     * @return a list containing the last calculated destinations for some chosen origin
     * @see VirtualPosition
     * @see PositionTreeNode
     */
    public List<VirtualPosition> getDestinations() {
        List<VirtualPosition> destinations = new ArrayList<>();
        for (PositionTreeNode destinationNode: this.destinations) {
            destinations.add(destinationNode.position);
        }
        return destinations;
    }

    /**
     * Takes a {@link Piece} and calculates all the possible moves originating at a position containing this piece.
     * @param  piece the piece to calculate moves for
     * @return a set containing all possible moves of piece parameter
     * @see Move
     */
    public Set<Move> getMoves(Piece piece) {
        Set<Move> moves = new HashSet<>();
        for (VirtualPosition source : getPositions(piece)) {
            clearDestinations();
            updateDestinations(source);
            for (PositionTreeNode destination : destinations) {
                moves.add(new Move(source, destination.position, piece, getMoveChain(destination.position)));
            }
        }
        return moves;
    }

    /**
     * Represents a tree node holding a position.
     * Used by State class as a data structure to hold calculated possible destinations.
     * @see VirtualPosition
     */
    private static class PositionTreeNode {

        private final VirtualPosition position;
        private PositionTreeNode parent;
        private final List<PositionTreeNode> children;

        private PositionTreeNode(VirtualPosition position, PositionTreeNode parent) {
            this(position);
            this.parent = parent;
        }

        private PositionTreeNode(VirtualPosition position) {
            this.position = position;
            this.children = new ArrayList<>();
        }

        private void addChild(PositionTreeNode child) {
            children.add(child);
        }

        @Override
        public boolean equals(Object o) {
            // compares the position field only
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PositionTreeNode that = (PositionTreeNode) o;
            return position.equals(that.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            print(buffer, "", "");
            return buffer.toString();
        }

        private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            buffer.append(position);
            buffer.append('\n');
            for (Iterator<PositionTreeNode> it = children.iterator(); it.hasNext();) {
                PositionTreeNode next = it.next();
                if (it.hasNext()) {
                    next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "|   ");
                } else {
                    next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "    ");
                }
            }
        }
    }
}
