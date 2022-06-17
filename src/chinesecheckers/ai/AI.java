package chinesecheckers.ai;

import chinesecheckers.util.Move;
import chinesecheckers.util.State;

/**
 * Chinese Checkers AI
 */
public interface AI {
    /**
     * Takes the current game {@link State} and chooses a move for the current player
     * according to the {@link Mode} specified.
     * @param state the current state of the game
     * @param mode the mode upon which to choose a move
     * @return the move chosen
     */
    Move decideMove(State state, Mode mode);
}
