package chinesecheckers.ui;

import chinesecheckers.ai.Minimax;
import chinesecheckers.util.VirtualPosition;
import chinesecheckers.util.State;
import chinesecheckers.util.Player;
import chinesecheckers.util.Move;
import chinesecheckers.util.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for all boards.
 * Defines the board UI that is not board-shape-specific as an instance of javax.swing.JPanel.
 */
public abstract class Board extends JPanel {

    private static final int WINDOW_SIDE_SIZE = 750;
    private static final int MINI_WINDOW_SIDE_SIZE = 400;
    private static final int MINI_BOARD_X_POSITION = 250;
    private static final int MINI_BOARD_Y_POSITION = 50;
    private static final int REGULAR_BOARD_BACKGROUND_SIZE = 735;
    private static final int MINI_BOARD_BACKGROUND_SIZE = 400;

    private static final int WINNER_LABEL_X_POSITION = 275;
    private static final int WINNER_LABEL_Y_POSITION = 75;
    private static final int WINNER_LABEL_WIDTH = 300;
    private static final int WINNER_LABEL_HEIGHT = 50;
    private static final int WINNER_LABEL_FONT_SIZE = 40;
    private static final String WINNER_LABEL_FONT = "Comic Sans";

    private static final int DELAY_BETWEEN_HOPS = 250;
    private static final int INITIAL_HOPS_DELAY = 150;

    private static Image regularBackGroundImage;
    private static Image miniBackGroundImage;
    private final Image backGroundImage;
    private List<VirtualPosition> moveChain;
    private int chainIndex;
    private Timer movePieceTimer;
    private JLabel winnerLabel;

    State state;
    final Theme theme;
    final int maxDistance;
    final List<Player> players;
    Map<VirtualPosition, Position> virtualPositionToPositionMap;

    static { // load images
        try {
            BufferedImage boardImage = ImageIO.read(new File("resources/images/board_background.png"));
            regularBackGroundImage = boardImage
                    .getScaledInstance(REGULAR_BOARD_BACKGROUND_SIZE, REGULAR_BOARD_BACKGROUND_SIZE, Image.SCALE_SMOOTH);
            BufferedImage miniBoardImage = ImageIO.read(new File("resources/images/mini_board_background.png"));
            miniBackGroundImage = miniBoardImage
                    .getScaledInstance(MINI_BOARD_BACKGROUND_SIZE, MINI_BOARD_BACKGROUND_SIZE, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            System.out.println("Error reading background image:");
            e.printStackTrace();
        }
    }

    Board(int maxDistance, ArrayList<Player> players, Theme theme, boolean mini) {
        this.maxDistance = maxDistance;
        this.players = players;
        this.theme = theme;
        this.setLayout(null);
        if (mini) { // generating mini board without game logic
            this.state = null;
            this.setSize(MINI_WINDOW_SIDE_SIZE, MINI_WINDOW_SIDE_SIZE);
            this.setBounds(MINI_BOARD_X_POSITION, MINI_BOARD_Y_POSITION,
                    MINI_WINDOW_SIDE_SIZE, MINI_WINDOW_SIDE_SIZE);
            this.backGroundImage = miniBackGroundImage;
        } else { // generating game board
            this.setSize(WINDOW_SIDE_SIZE, WINDOW_SIDE_SIZE);
            this.backGroundImage = regularBackGroundImage;
            this.setBackground(Color.LIGHT_GRAY);
            this.winnerLabel = new JLabel();
            this.winnerLabel.setBounds(WINNER_LABEL_X_POSITION, WINNER_LABEL_Y_POSITION,
                    WINNER_LABEL_WIDTH, WINNER_LABEL_HEIGHT);
            this.winnerLabel.setFont(new Font(WINNER_LABEL_FONT, Font.BOLD, WINNER_LABEL_FONT_SIZE));
            this.add(winnerLabel);
            this.state = new State(players, maxDistance, getDirections()); // a state object for the game
            this.addPositions(false); // initializing board specific state
            this.state.updatePointPositionMap(); // finish initializing state
            this.state.updateCenter(); // finish initializing state
            this.state.updateFarthestPositions(); // finish initializing state
            this.movePieceTimer = new Timer(DELAY_BETWEEN_HOPS, this::movePiece); // initialize move chain timer
            this.movePieceTimer.setInitialDelay(INITIAL_HOPS_DELAY);
            if (players.get(0).isAI()) { // start game
                SwingUtilities.invokeLater(() -> {
                    Move move = getAIMove(this.players.get(0));
                    this.moveChain = move.getMoveChain();
                    this.movePieceTimer.start();
                });
            } // else wait for human to make first move
        }
    }

    // board specific methods
    abstract void addPositions(boolean mini); // post condition: virtualPositionToPositionMap is initialized with all positions
    abstract int[][] getDirections();

    void selectPosition(ActionEvent e) {
        if (state.getWinner() == null) {
            Position position = (Position) e.getSource();
            if (position.getPiece() == Piece.NONE && state.isDestination(position.getVirtualPosition())) {
                // destination choice
                moveChain = state.getMoveChain(position.getVirtualPosition());
                movePieceTimer.start();
                unMarkDestinations();
            }
            else if (position.getPiece() == state.getCurrentPlayer().getPiece() && state.getDestinations().isEmpty()) {
                // origin choice
                markDestinations(position);
            }
            else if (position.getPiece() == state.getCurrentPlayer().getPiece()) {
                // switching to different origin choice
                unMarkDestinations();
                state.clearDestinations();
                markDestinations(position);
                state.updateDestinations(position.getVirtualPosition());
            }
            else if (!state.getDestinations().isEmpty()) {
                // non active position choice
                unMarkDestinations();
                state.clearDestinations();
                state.clearDestinations();
            }
        }
    }
    private void markDestinations(Position origin) { // highlights possible moves
        state.updateDestinations(origin.getVirtualPosition());
        for (VirtualPosition virtualDestination : state.getDestinations()) {
            Position destination = virtualPositionToPositionMap.get(virtualDestination);
            destination.setIcon(theme.getMarkedPositionImageIcon());
        }
    }
    private void unMarkDestinations() { // remove highlight from possible moves
        for (VirtualPosition virtualDestination : state.getDestinations()) {
            Position destination = virtualPositionToPositionMap.get(virtualDestination);
            destination.setIcon(theme.getImageIcon(destination.getPiece()));
        }
    }

    private void movePiece(ActionEvent e) { // moves one step of a move
        Position currentOrigin = virtualPositionToPositionMap.get(moveChain.get(chainIndex));
        Position currentDestination = virtualPositionToPositionMap.get(moveChain.get(chainIndex + 1));
        currentDestination.setPiece(moveChain.get(chainIndex).getPiece());
        currentDestination.setIcon(theme.getImageIcon(moveChain.get(chainIndex).getPiece()));
        currentOrigin.setPiece(Piece.NONE);
        currentOrigin.setIcon(theme.getImageIcon(Piece.NONE));
        chainIndex++;
        if (chainIndex == moveChain.size() - 1) { // last step in chain
            VirtualPosition origin = moveChain.get(0);
            VirtualPosition destination = moveChain.get(moveChain.size() - 1);
            state.movePiece(new Move(origin, destination, destination.getPiece())); // update state accordingly
            unMarkDestinations();
            Player player = state.getPiecePlayerMap().get(destination.getPiece());
            chainIndex = 0;
            this.movePieceTimer.stop();
            if (state.isWinner(player)) { // check win
                state.setWinner(player);
                winnerLabel.setText(player + " wins");
            } else { // continue game
                SwingUtilities.invokeLater( () -> {
                    state.changeTurn();
                    if (state.getCurrentPlayer().isAI()) {
                        Move move = getAIMove(state.getCurrentPlayer());
                        moveChain = move.getMoveChain();
                        movePieceTimer.start();
                    } // else waiting for human to make move
                });

            }
        }
    }

    private Move getAIMove(Player player) { // gets an AI move
        return new Minimax(state).decideMove(new State(state), player.getMode());
    }

    List<Player> getPlayers() { // players of the game
        return players;
    }

    @Override
    protected void paintComponent(Graphics g) { // paints background image
        super.paintComponent(g);
        g.drawImage(backGroundImage, 0, 0, this);
    }
}
