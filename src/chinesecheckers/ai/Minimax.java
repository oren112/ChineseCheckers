package chinesecheckers.ai;

import chinesecheckers.util.VirtualPosition;
import chinesecheckers.util.State;
import chinesecheckers.util.Move;
import chinesecheckers.util.Piece;
import chinesecheckers.util.Player;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Represents a deterministic AI based on the Minimax algorithm with a randomized final tie breaker.
 */
public class Minimax implements AI {

    private static final int END_GAME_DISTANCE_BOUNDARY = 4;
    private static final int MID_GAME_DISTANCE_BOUNDARY = 5;
    private static final int CROWDED_END_ZONE_RATIO = 2;
    private static final int EASY_DECISION_TREE_DEPTH = 1;
    private static final int NORMAL_DECISION_TREE_DEPTH = 2;
    private static final int HARD_DECISION_TREE_DEPTH = 3;
    private static final int END_GAME_DECISION_TREE_DEPTH = 3;
    private static final int NUM_FILTERED_MOVES_BLOCKING = 3;
    private static final int NUM_FILTERED_MOVES_EASY = 4;
    private static final int NUM_FILTERED_MOVES_NORMAL = 2;
    private static final int NUM_FILTERED_MOVES_HARD = 8;
    private static final int NUM_FILTERED_MOVES_END_GAME = 12;

    private final State state;
    private final boolean midGame;
    private final Player leadingOpponent;

    /**
     * Creates a Minimax AI tuned to the given state of the game.
     * @param state the state the Minimax is based on.
     */
    public Minimax(State state) {
        this.state = state;
        this.midGame = isMidGame();
        this.leadingOpponent = leadingOpponent();
    }

    /**
     * Takes the current game {@link State} and chooses a move for the current player
     * according to the {@link Mode} specified, based on the Minimax algorithm.
     * The algorithm is generalized to more than two player with maximizing own win chance
     * and minimizing the leading opponent's winning chance.
     * The algorithm also checks for additional boundary cases which are not included in the traditional Minimax,
     * to optimize choice quality.
     * @param state Represents the state upon which to decide AI move
     * @param mode Represents the quality of the move to be decided
     * @return Move object representing the decided move
     */
    @Override
    public Move decideMove(State state, Mode mode) {
        DecisionTreeNode root = new DecisionTreeNode(state, state.getCurrentPlayer().getPiece());
        boolean endGame = isEndGame();
        int decisionTreeDepth;
        if (endGame) { // near end of game more resources are needed
            decisionTreeDepth = END_GAME_DECISION_TREE_DEPTH;
            mode = Mode.ENDGAME;
        } else if (mode == Mode.HARD) {
            decisionTreeDepth = HARD_DECISION_TREE_DEPTH;
            mode = Mode.HARD;
        } else if (mode == Mode.NORMAL) {
            decisionTreeDepth = NORMAL_DECISION_TREE_DEPTH;
        } else {
            decisionTreeDepth = EASY_DECISION_TREE_DEPTH;
        }
        generateDecisionTree(root, state.getCurrentPlayer().getPiece(),
                decisionTreeDepth, mode, endGame, blockingPositions());
        // chosen move is in one of the root children, find it in the child with the same heuristic
        // value as the root
        for (DecisionTreeNode child : root.children) { // finish the algorithm by choosing the correct move
            if (child.heuristicValue == root.heuristicValue) {
                return child.lastMove;
            }
        }
        // couldn't find a matching child, this means logical error in the algorithm, shouldn't ever get here
        return null;
    }

    // * generates a Minimax decision tree rooted at the DecisionNodeTree root parameter.
    // * flow: the current player chooses a set of moves, and recursively calculates
    //   possible responses by the other players. finally, it chooses the move that will
    //   likely lead to the best outcome a few moves ahead (the depth parameter is responsible
    //   for the number of moves ahead it calculates).
    // * if endGame is set to true, the algorithm will not take in account other players moves and will
    //   only calculate the fastest way to victory.
    // * if a winning state is achieved for current player it will not proceed to create more branches.
    private int generateDecisionTree(DecisionTreeNode root, Piece piece, int height, Mode mode,
                                     boolean endGame, List<VirtualPosition> blockingPositions) {
        if (height == 0) {
            int heuristicValue = heuristicValue(root.state);
            root.heuristicValue = heuristicValue;
            return heuristicValue;
        }
        Set<Move> moves;
        if (blockingPositions != null && blockingPositions.size() > 0) {
            Iterator<VirtualPosition> blockingPositionsIterator = blockingPositions.iterator();
            do {
                moves = filterMoves(root.state, root.state.getMoves(piece), mode, piece, blockingPositionsIterator.next());
            } while (moves.size() == 0 && blockingPositionsIterator.hasNext());
            if (moves.isEmpty()) { // if can't move the blocking piece get other moves
                // this means there is a blocking position but can't move it
                moves = filterMoves(root.state, root.state.getMoves(piece), mode, piece, null);
            }
        } else {
            moves = filterMoves(root.state, root.state.getMoves(piece), mode, piece, null);
        }

        for (Move move : moves) {
            // the next player plays a move based on previous moves
            State state = root.state;
            Piece nextPiece = nextPiece(endGame, state);
            DecisionTreeNode child = new DecisionTreeNode(state, move, nextPiece);
            root.addChild(child);
            if (piece != state.getCurrentPlayer().getPiece() || !isWinState(child.state)) { // not win state for this.player
                int heuristicValue = generateDecisionTree(child, nextPiece, height - 1, mode, endGame, null);
                if (minimize(piece) && heuristicValue < root.heuristicValue) {
                    root.heuristicValue = heuristicValue;
                } else if (!minimize(piece) && heuristicValue > root.heuristicValue) {
                    root.heuristicValue = heuristicValue;
                }
            } else { // reached win state in child for this.player
                // put the minimum value possible with a height factor such that a higher node is preferred
                child.heuristicValue = -height - state.getMaxDistance() * state.getNumPieces() * state.getPlayers().size();
                root.heuristicValue = -height - state.getMaxDistance() * state.getNumPieces() * state.getPlayers().size();
                break; // no need to check other moves as we have reached a win state which is best possible
            }
        }
        return root.heuristicValue;
    }

    private Player leadingOpponent() {
        Player leadingOpponent = null;
        int leadingOpponentDistance = Integer.MAX_VALUE;
        for (Player player: state.getPlayers()) {
            if (player.getPiece() != this.state.getCurrentPlayer().getPiece()) {
                int opponentDistance = state.getPlayerDistance(player);
                if (opponentDistance < leadingOpponentDistance) {
                    leadingOpponent = player;
                    leadingOpponentDistance = opponentDistance;
                }
            }
        }
        return leadingOpponent;
    }

    // * returns the heuristic value of a state
    // * the heuristic value is calculated as the distance the current player has to pass to reach victory,
    //   minus the sum of distances the other players have to pass to reach victory
    // * this fulfills the Minimax attribute where the current player tries to minimize the value
    //   assuming other players try to maximize it
    private int heuristicValue(State state) {
//        int hValue = 0;

//        for (Player player : state.getPlayers()) {
//            if (player.getPiece() == this.state.getCurrentPlayer().getPiece()) {
//                hValue += state.getPlayerDistance(player);
//            } else {
//                hValue -= state.getPlayerDistance(player);
//            }
//        }
//        return hValue;
        return state.getPlayerDistance(this.state.getCurrentPlayer()) - state.getPlayerDistance(leadingOpponent);
    }

    // * only this.player try to minimize the heuristic value, others try to maximize
    private boolean minimize(Piece piece) {
        return piece == state.getCurrentPlayer().getPiece();
    }

    // win state is when all the player's pieces reach the opposite corner
    private boolean isWinState(State state) {
        return state.getEndZonePositions(this.state.getCurrentPlayer().getPiece())
                .containsAll(state.getPositions(this.state.getCurrentPlayer().getPiece()));
    }

    // * end game is when the player is close to victory and hence chances are it depends only on itself.
    //   we use this assumption to give all the computational power to calculate own moves rather
    //   than also calculating opponent responses.
    // * returns true if all positions are of distance less than or equal to 3 from end zone, false otherwise.
    private boolean isEndGame() {
        for (VirtualPosition position : state.getPositions(state.getCurrentPlayer().getPiece())) {
            if (state.distance(position, state.getFarthestPosition(state.getCurrentPlayer())) > END_GAME_DISTANCE_BOUNDARY) {
                return false;
            }
        }
        return true;
    }

    // * returns true if all positions are of distance less than or equal to 5 from end zone, false otherwise.
    private boolean isMidGame() {
        for (VirtualPosition position : state.getPositions(state.getCurrentPlayer().getPiece())) {
            if (state.distanceToFreeEndZone(state.getCurrentPlayer().getPiece(), position) > MID_GAME_DISTANCE_BOUNDARY) {
                return false;
            }
        }
        return true;
    }

    // * calculates and returns the blocking positions of the current player
    // * definition: a blocking position is defined as a position residing in a crowded opponent end zone.
    //               an opponent end zone is considered crowded when the ratio between the end zone size and
    //               the of number pieces in it is grater than CROWDED_END_ZONE_RATIO.
    // * use case: blocking positions are bad for the blocking player since there is a high chance
    //             the blocking piece will be locked in the end zone for a long period of time.
    //             this both lowers own win chance and increases the opponent owning this end zone win chance.
    //             if a blocking position is detected it gets the highest priority to be moved.
    private List<VirtualPosition> blockingPositions() {
        List<VirtualPosition> blockingPositions = new ArrayList<>();
        for (VirtualPosition position : state.getPositions(state.getCurrentPlayer().getPiece())) {
            for (Player player : state.getPlayers()) { // checks if position is in a crowded endZone of some player
                if (!player.equals(state.getCurrentPlayer())
                        && state.getEndZonePositions(player.getPiece()).contains(position)
                        && state.getNumOtherPiecesInEndZone(player.getPiece(), state.getCurrentPlayer().getPiece())
                        >= state.getNumPieces() / CROWDED_END_ZONE_RATIO) {
                    blockingPositions.add(position);
                    break; // can only be in one end zone
                }
            }
        }
        if (blockingPositions.size() == 0) {
            return blockingPositions; // return an empty list
        }
        return blockingPositions.stream() // deepest in endZone (= farthest from center) will be first
                .sorted(Comparator.comparing(position -> -state.distance(position, state.getCenter())))
                .collect(Collectors.toList());
    }

    // if endGame is set to true then the function returns the current player piece,
    // otherwise it returns the next player piece
    private Piece nextPiece(boolean endGame, State state) {
        if (endGame) {
            return this.state.getCurrentPlayer().getPiece();
        }
        return state.nextTurn().getPiece();
    }

    // returns true if the move is towards the current player's end zone, otherwise false
    private boolean isForward(State state, Move move, Piece piece) {
        return state.distanceToFreeEndZone(piece, move.getDestination())
                < state.distanceToFreeEndZone(piece, move.getOrigin());
    }

    // returns true the move is not towards the current player's initial positions, otherwise false
    private boolean isNotBackward(State state, Move move, Piece piece) {
        return state.distanceToFreeEndZone(piece, move.getDestination())
                <= state.distanceToFreeEndZone(piece, move.getOrigin());
    }

    // returns the distance covered in this move with a negative sign
    private int moveDelta(State state, Move move, Piece piece) {
        return state.distanceToFreeEndZone(piece, move.getDestination())
                - state.distanceToFreeEndZone(piece, move.getOrigin());
    }

    // calculates the distance from the destination of the move and the center
    // a sign is added to the calculation since this function is for sorting purposes
    // if the piece is in its own end zone it returns the value with a negative sign
    // else if the piece is in an opponent end zone it returns the value with a positive sign
    // otherwise returns 0
    private int moveEdgeDelta(State state, Move move, Piece piece) {
        int edgeDelta = state.distance(move.getDestination(), state.getCenter());
        if (state.getEndZonePositions(piece).contains(move.getDestination())) { // own end zone
            return -edgeDelta;
        }
        for (Player player : state.getPlayers()) {
            if (player != state.getCurrentPlayer()
                    && state.getEndZonePositions(player.getPiece()).contains(move.getDestination())) { // opponent end zone
                return edgeDelta;
            }
        }
        return 0;
    }

    // returns true if the move is from outside an opponent crowded end zone to an opponent crowded end zone
    private boolean isToCrowdedEndZone(State state, Move move, Piece piece) {
        for (Player player : state.getPlayers()) {
            if (player != state.getCurrentPlayer()
                    && state.getNumOtherPiecesInEndZone(piece, state.getCurrentPlayer().getPiece())
                    >= state.getNumPieces() / CROWDED_END_ZONE_RATIO
                    && state.getEndZonePositions(player.getPiece()).contains(move.getDestination())
                    && !state.getEndZonePositions(player.getPiece()).contains(move.getOrigin())) {
                return true;
            }
        }
        return false;
    }

    // filters all possible moves to a subset based on the mode and the game state
    private Set<Move> filterMoves(State state, Set<Move> moves, Mode mode, Piece piece, VirtualPosition blockingPosition) {
        if (blockingPosition != null) {
            return moves
                    .stream()
                    .filter(move -> move.getOrigin().equals(blockingPosition)) // if there are blocking positions use only them
                    .filter(move -> isForward(state, move, piece))
                    .sorted(Comparator.comparing(move -> -state.distance(move.getOrigin(), state.getCenter()))) // farthest from center first
                    .limit(NUM_FILTERED_MOVES_BLOCKING)
                    .collect(Collectors.toSet());
        }
        if (mode == Mode.EASY) {
            return moves // use only moves that are towards end zone
                    .stream()
                    .filter(move -> isForward(state, move, piece))
                    .limit(NUM_FILTERED_MOVES_EASY)
                    .collect(Collectors.toSet());
        }
        if (mode == Mode.ENDGAME) {
            return moves // use moves that are not backwards
                    .stream()
                    .filter(move -> isNotBackward(state, move, piece))
                    .limit(NUM_FILTERED_MOVES_END_GAME)
                    .collect(Collectors.toSet());
        }

        // get here when mode is NORMAL or HARD, it is not end game, and there are no blocking positions of current player
        int sign = midGame ? 1 : -1;
        return moves
                .stream()
                .filter(move -> !isToCrowdedEndZone(state, move, piece)) // take only moves that are not to a crowded opponent end zone
                .sorted(Comparator.comparing(move -> moveDelta(state, (Move) move, piece)) // prefer highest distance to end zone covered first
                        .thenComparing(move -> sign * state.distance(((Move) move).getOrigin(), // if midGame prefer positions near farthest position
                                state.getFarthestPosition(state.getCurrentPlayer()))) // else prefer positions near home
                        .thenComparing(move -> state.distance(((Move) move).getDestination(), // prefer moves deeper in end zone
                                state.getFarthestPosition(state.getCurrentPlayer())))
                        .thenComparing(move -> moveEdgeDelta(state, (Move) move, piece))) // if entered opponent (own) end zone prefer close (far) from center
                .limit(mode == Mode.NORMAL ? NUM_FILTERED_MOVES_NORMAL : NUM_FILTERED_MOVES_HARD)
                .collect(Collectors.toSet());
    }

    // a class to represent a decision tree node for the minimax algorithm
    private class DecisionTreeNode {

        private final State state;
        private final Set<DecisionTreeNode> children;
        private final Move lastMove;
        private int heuristicValue;

        private DecisionTreeNode(State state, Piece piece) {
            if (Minimax.this.state.getCurrentPlayer().getPiece() == piece) {
                this.heuristicValue = Integer.MAX_VALUE;
            } else {
                this.heuristicValue = Integer.MIN_VALUE;
            }
            this.state = new State(state);
            this.children = new HashSet<>();
            this.lastMove = null;
        }

        private DecisionTreeNode(State state, Move lastMove, Piece piece) {
            this.lastMove = lastMove; // keep track of last move made for the Minimax to get its final decision
            this.state = new State(state, lastMove);
            if (!isEndGame()) {
                this.state.changeTurn();
            }
            if (Minimax.this.state.getCurrentPlayer().getPiece() == piece) {
                this.heuristicValue = Integer.MAX_VALUE;
            } else {
                this.heuristicValue = Integer.MIN_VALUE;
            }
            this.children = new HashSet<>();
        }

        private void addChild(DecisionTreeNode child) {
            this.children.add(child);
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            print(buffer, "", "");
            return buffer.toString();
        }

        private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            buffer.append(" hVal: ").append(heuristicValue);
            buffer.append(" piece: ").append(state.getCurrentPlayer().getPiece());
            buffer.append(" move: ").append(lastMove);
            buffer.append('\n');
            for (Iterator<DecisionTreeNode> it = children.iterator(); it.hasNext();) {
                DecisionTreeNode next = it.next();
                if (it.hasNext()) {
                    next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "|   ");
                } else {
                    next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "    ");
                }
            }
        }

    }
}

