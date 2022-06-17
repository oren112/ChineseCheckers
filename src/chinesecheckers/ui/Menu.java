package chinesecheckers.ui;

import chinesecheckers.util.Player;
import chinesecheckers.util.Piece;
import chinesecheckers.ai.Mode;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Represents the game menu which starts the event loop containing the sub menus and the game.
 */
public class Menu {
    private static final JPanel howToPlayPanel = new JPanel();
    private static final JPanel aboutTheCodePanel = new JPanel();

    private static final JFrame window = new JFrame();
    private static final JPanel mainMenu = new JPanel();
    private static final JPanel newGameMenu = new JPanel();
    private static final String[] boardShapeOptions = {"board shape", "square", "star"};
    private static final String[] playerTypeOptions = {"player type", "human", "easy AI", "normal AI", "hard AI"};
    private static final String[] squareNumPlayersOptions = {"number of players", "2", "4"};
    private static final String[] starOfDavidNumPlayersOptions = {"number of players", "2", "4", "6"};
    private static final DefaultComboBoxModel<String> squareNumPlayersOptionsModel = new DefaultComboBoxModel<>(squareNumPlayersOptions);
    private static final DefaultComboBoxModel<String> starOfDavidNumPlayersOptionsModel = new DefaultComboBoxModel<>(starOfDavidNumPlayersOptions);
    private static final ChoicePane boardShapeChoicePane = new ChoicePane(100, boardShapeOptions, Color.WHITE, true);
    private static final ChoicePane numPlayersChoicePane = new ChoicePane(150, squareNumPlayersOptions, Color.WHITE, true);
    private static final ChoicePane playerTypeChoicePaneWhite = new ChoicePane(200, playerTypeOptions, Color.WHITE, false);
    private static final ChoicePane playerTypeChoicePaneBlack = new ChoicePane(250, playerTypeOptions, Color.BLACK, false);
    private static final ChoicePane playerTypeChoicePaneYellow = new ChoicePane(300, playerTypeOptions, Color.YELLOW, false);
    private static final ChoicePane playerTypeChoicePaneBlue = new ChoicePane(350, playerTypeOptions, Color.BLUE.darker(), false);
    private static final ChoicePane playerTypeChoicePaneGreen = new ChoicePane(400, playerTypeOptions, Color.GREEN.darker(), false);
    private static final ChoicePane playerTypeChoicePaneRed = new ChoicePane(450, playerTypeOptions, Color.RED.darker(), false);
    static {
        playerTypeChoicePaneWhite.comboBox.addItemListener(e -> updatePlayer(e, "WHITE"));
        playerTypeChoicePaneBlack.comboBox.addItemListener(e -> updatePlayer(e, "BLACK"));
        playerTypeChoicePaneYellow.comboBox.addItemListener(e -> updatePlayer(e, "YELLOW"));
        playerTypeChoicePaneBlue.comboBox.addItemListener(e -> updatePlayer(e, "BLUE"));
        playerTypeChoicePaneGreen.comboBox.addItemListener(e -> updatePlayer(e, "GREEN"));
        playerTypeChoicePaneRed.comboBox.addItemListener(e -> updatePlayer(e, "RED"));
    }
    private static void updatePlayer(ItemEvent e, String color) {
        Player player;
        switch (color) {
            case "WHITE":
                player = players.get(0);
                break;
            case "BLACK":
                if (players.size() == 2) player = players.get(1);
                else if (players.size() == 4) player = players.get(2);
                else player = players.get(3);
                break;
            case "YELLOW":
                player = players.get(1);
                break;
            case "BLUE":
                if (players.size() == 4) player = players.get(3);
                else player = players.get(4);
                break;
            case "GREEN":
                player = players.get(5);
                break;
            default: // "RED"
                player = players.get(2);
                break;
        }
        String playerType = (String) e.getItem();
        switch (playerType) {
            case "human":
                player.setHuman(true);
                break;
            case "easy AI":
                player.setHuman(false);
                player.setMode(Mode.EASY);
                break;
            case "normal AI":
                player.setHuman(false);
                player.setMode(Mode.NORMAL);
                break;
            case "hard AI":
                player.setHuman(false);
                player.setMode(Mode.HARD);
                break;
        }
    }

    private static final ClassicTheme classicTheme = new ClassicTheme();
    private static ArrayList<Player> players = new ArrayList<>();
    private static Board board = new SquareBoard(new ArrayList<>(), classicTheme, true);

    private static Color getForeGroundColor(Color bgColor) {
        if (bgColor.equals(Color.WHITE)) return Color.BLACK;
        if (bgColor.equals(Color.BLACK)) return Color.WHITE;
        if (bgColor.equals(Color.YELLOW)) return Color.BLACK;
        if (bgColor.equals(Color.BLUE.darker())) return Color.WHITE;
        if (bgColor.equals(Color.GREEN.darker())) return Color.WHITE;
        if (bgColor.equals(Color.RED.darker())) return Color.WHITE;
        return null;
    }

    private static void initWindow() {
        window.setLocationByPlatform(true);
        window.setTitle("chinese checkers");
        window.setSize(750, 775);
        window.setIconImage((new ImageIcon("logo.png")).getImage());
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.getContentPane().add(mainMenu);
        window.setVisible(true);
    }

    private static void initMainMenu() {
        mainMenu.setLayout(null);
        mainMenu.setSize(750,775);
        MenuButton newGameButton = new MenuButton("new game", 100, 75);
        MenuButton howToPlayButton = new MenuButton("how to play", 100, 225);
        MenuButton aboutTheCodeButton = new MenuButton("about the code", 100, 375);
        mainMenu.add(newGameButton);
        mainMenu.add(howToPlayButton);
        mainMenu.add(aboutTheCodeButton);
        mainMenu.setVisible(true);

        newGameButton.addActionListener(Menu::startNewGameMenu);
        howToPlayButton.addActionListener(e -> {
            // switch to howToPlayPanel
            window.getContentPane().removeAll();
            window.getContentPane().add(howToPlayPanel);
            window.revalidate();
            window.repaint();
        });
        aboutTheCodeButton.addActionListener(e -> {
            // switch to aboutTheCodeMenu
            window.getContentPane().removeAll();
            window.getContentPane().add(aboutTheCodePanel);
            window.revalidate();
            window.repaint();
        });
    }
    private static void startNewGameMenu(ActionEvent e) {
        boardShapeChoicePane.comboBox.setSelectedIndex(0);
        boardShapeChoicePane.comboBox.setSelectedIndex(1);
        window.getContentPane().removeAll();
        window.getContentPane().add(newGameMenu);
        window.revalidate();
        window.repaint();
    }

    private static void startMainMenu(ActionEvent e) {
        window.getContentPane().removeAll();
        window.getContentPane().add(mainMenu);
        window.revalidate();
        window.repaint();
    }

    private static void updateMiniBoard(String boardShape) {
        players = new ArrayList<>();
        newGameMenu.remove(board);
        if (boardShape.equals("square")) {
            board = new SquareBoard(players, classicTheme, true);
            ((ChoicePane) newGameMenu.getComponent(1)).comboBox.setModel(squareNumPlayersOptionsModel);
        }
        else if (boardShape.equals("star")) {
            board = new StarBoard(players, classicTheme, true);
            ((ChoicePane) newGameMenu.getComponent(1)).comboBox.setModel(starOfDavidNumPlayersOptionsModel);
        }
        board.addPositions(true);
        newGameMenu.add(board);
        numPlayersChoicePane.comboBox.setSelectedIndex(0);
        numPlayersChoicePane.comboBox.setSelectedIndex(1);
    }

    private static void initNewGameMenu() {
        newGameMenu.setLayout(null);
        newGameMenu.setSize(750,775);
        newGameMenu.setVisible(true);
        newGameMenu.add(board);


        boardShapeChoicePane.comboBox.addItemListener(e -> {
            String boardShape = (String) e.getItem();
            updateMiniBoard(boardShape);
        });
        newGameMenu.add(boardShapeChoicePane);

        numPlayersChoicePane.comboBox.addItemListener(e -> {
            String numPlayers = (String) e.getItem();
            initPlayers(numPlayers);
            if (numPlayers.equals("2") || numPlayers.equals("4") || numPlayers.equals("6")) {
                if (board instanceof SquareBoard) {
                    newGameMenu.remove(board);
                    board = new SquareBoard(players, classicTheme, true);
                    newGameMenu.add(board);
                }
                if (board instanceof StarBoard) {
                    newGameMenu.remove(board);
                    board = new StarBoard(players, classicTheme, true);
                    newGameMenu.add(board);
                }
                board.addPositions(true);
                board.revalidate();
                board.repaint();
                newGameMenu.revalidate();
                newGameMenu.repaint();
                showPlayerTypeChoices(numPlayers);
            }
        });
        newGameMenu.add(numPlayersChoicePane);
        newGameMenu.add(playerTypeChoicePaneWhite);
        newGameMenu.add(playerTypeChoicePaneBlack);
        newGameMenu.add(playerTypeChoicePaneYellow);
        newGameMenu.add(playerTypeChoicePaneBlue);
        newGameMenu.add(playerTypeChoicePaneGreen);
        newGameMenu.add(playerTypeChoicePaneRed);
        JButton startGameButton = new MenuButton("start", 100, 500);
        startGameButton.addActionListener(Menu::start);
        newGameMenu.add(startGameButton);
        BoardButton mainMenuButton = new BoardButton("main menu", 30, 30);
        mainMenuButton.addActionListener(Menu::startMainMenu);
        newGameMenu.add(mainMenuButton);
    }

    private static void initPlayers(String numPlayers) {
        players = new ArrayList<>();
        switch (numPlayers) {
            case "2":
                players.add(new Player(Piece.WHITE, false));
                players.add(new Player(Piece.BLACK, false));
                break;
            case "4":
                players.add(new Player(Piece.WHITE, false));
                players.add(new Player(Piece.YELLOW, false));
                players.add(new Player(Piece.BLACK, false));
                players.add(new Player(Piece.BLUE, false));
                break;
            case "6":
                players.add(new Player(Piece.WHITE, false));
                players.add(new Player(Piece.YELLOW, false));
                players.add(new Player(Piece.RED, false));
                players.add(new Player(Piece.BLACK, false));
                players.add(new Player(Piece.BLUE, false));
                players.add(new Player(Piece.GREEN, false));
                break;
        }
    }

    private static void start(ActionEvent e) {
        newGameMenu.remove(board);
        newGameMenu.revalidate();
        newGameMenu.repaint();
        window.getContentPane().removeAll();
        if (board instanceof SquareBoard) board = new SquareBoard(players, classicTheme, false);
        if (board instanceof StarBoard) board = new StarBoard(players, classicTheme, false);
        BoardButton restartButton = new BoardButton("restart", 30, 30);
        restartButton.addActionListener(Menu::start);
        BoardButton newGameButton = new BoardButton("new game", 600, 30);
        newGameButton.addActionListener(Menu::startNewGameMenu);
        BoardButton mainMenuButton = new BoardButton("main menu", 30, 660);
        mainMenuButton.addActionListener(Menu::startMainMenu);
        board.add(restartButton);
        board.add(newGameButton);
        board.add(mainMenuButton);
        window.getContentPane().add(board);
        window.invalidate();
        window.revalidate();
    }

    private static void initAboutTheCodePanel() {
        aboutTheCodePanel.setLayout(null);
        aboutTheCodePanel.setSize(750,775);
        aboutTheCodePanel.setVisible(true);
        BoardButton mainMenuButton = new BoardButton("main menu", 30, 30);
        mainMenuButton.addActionListener(Menu::startMainMenu);
        MenuButton javaDoc = new MenuButton("<html><u>JavaDoc in default browser</u></html>", 0, 0);
        javaDoc.setBounds(50, 150, 600, 50);
        javaDoc.setFont(new Font("Comic Sans", Font.ITALIC, 33));
        javaDoc.setBorder(null);
        javaDoc.addActionListener(actionEvent -> {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new File(System.getProperty("user.dir") + "\\javadoc\\index.html").toURI());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        JLabel openOption = new JLabel("or manually from ChineseCheckers/javadoc/index.html");
        openOption.setFont(new Font("Comic Sans", Font.ITALIC, 18));
        openOption.setBounds(150, 200, 500, 50);
        openOption.setForeground(Color.decode("#8b0000"));
        aboutTheCodePanel.add(javaDoc);
        aboutTheCodePanel.add(openOption);
        aboutTheCodePanel.add(mainMenuButton);
    }

    private static void initHowToPlayPanel() {
        howToPlayPanel.setLayout(null);
        howToPlayPanel.setSize(750,775);
        howToPlayPanel.setVisible(true);
        String rules = "<html>" +
                "<h1>rules</h1>" +
                "First player to move all his pieces to the opposite corner is the winner" + "<br/><br/>" +
                "Two types of moves:" + "<br/>" +
                "<ul><li>Moving one step to a neighbor empty position</li>" +
                "<li>A chain of one or more hops not landing at the origin position</li></ul>" +
                "A hop is a move over a single non empty position to an empty position" + "<br/><br/>" +
                "Alternative victory:<br/><br/>If at least one of the player's pieces is in the opposite corner" + "<br/>" +
                "and the corner has no empty positions it is also considered a victory" + "</html>";
        JLabel howToPlay = new JLabel(rules);
        howToPlay.setFont(new Font("Comic Sans", Font.ITALIC, 20));
        howToPlay.setBounds(100, 50, 550, 550);
        howToPlay.setForeground(Color.decode("#8b0000"));
        BoardButton mainMenuButton = new BoardButton("main menu", 30, 30);
        mainMenuButton.addActionListener(Menu::startMainMenu);
        howToPlayPanel.add(howToPlay);
        howToPlayPanel.add(mainMenuButton);
    }

    private static void showPlayerTypeChoices(String numPlayers) {
        playerTypeChoicePaneYellow.setVisible(false);
        playerTypeChoicePaneBlue.setVisible(false);
        playerTypeChoicePaneGreen.setVisible(false);
        playerTypeChoicePaneRed.setVisible(false);
        playerTypeChoicePaneWhite.comboBox.setSelectedIndex(0);
        playerTypeChoicePaneBlack.comboBox.setSelectedIndex(0);
        playerTypeChoicePaneWhite.comboBox.setSelectedIndex(3);
        playerTypeChoicePaneBlack.comboBox.setSelectedIndex(1);
        switch (numPlayers) {
            case "2":
                playerTypeChoicePaneWhite.setVisible(true);
                playerTypeChoicePaneBlack.setVisible(true);
                break;
            case "4":
                playerTypeChoicePaneWhite.setVisible(true);
                playerTypeChoicePaneBlack.setVisible(true);
                playerTypeChoicePaneYellow.setVisible(true);
                playerTypeChoicePaneBlue.setVisible(true);
                playerTypeChoicePaneYellow.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneBlue.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneYellow.comboBox.setSelectedIndex(3);
                playerTypeChoicePaneBlue.comboBox.setSelectedIndex(3);
                break;
            case "6":
                playerTypeChoicePaneWhite.setVisible(true);
                playerTypeChoicePaneBlack.setVisible(true);
                playerTypeChoicePaneYellow.setVisible(true);
                playerTypeChoicePaneBlue.setVisible(true);
                playerTypeChoicePaneGreen.setVisible(true);
                playerTypeChoicePaneRed.setVisible(true);
                playerTypeChoicePaneYellow.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneBlue.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneGreen.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneRed.comboBox.setSelectedIndex(0);
                playerTypeChoicePaneYellow.comboBox.setSelectedIndex(3);
                playerTypeChoicePaneBlue.comboBox.setSelectedIndex(3);
                playerTypeChoicePaneGreen.comboBox.setSelectedIndex(3);
                playerTypeChoicePaneRed.comboBox.setSelectedIndex(3);
                break;
        }
    }

    /**
     * Initializes the menu and launches the event loop
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            initAboutTheCodePanel();
            initHowToPlayPanel();
            initNewGameMenu();
            initMainMenu();
            initWindow();
        });
    }

    private static class MenuButton extends JButton { // reusable menu button
        public MenuButton(String text, int x, int y) {
            this.setText(text);
            this.setForeground(Color.decode("#8b0000"));
            this.setBounds(x, y, 550, 100);
            this.setOpaque(false);
            this.setContentAreaFilled(false);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
            this.setFont(new Font("Comic Sans", Font.ITALIC, 45));
            this.setHorizontalTextPosition(JButton.CENTER);
            this.setVerticalTextPosition(JButton.CENTER);
            this.setFocusable(false);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private static class BoardButton extends JButton { // reusable menu button
        private BoardButton(String text, int x, int y) {
            this.setText(text);
            this.setForeground(Color.decode("#8b0000"));
            this.setBounds(x, y, 100, 50);
            this.setOpaque(false);
            this.setContentAreaFilled(false);
            this.setBorder(BorderFactory.createLineBorder(Color.decode("#8b0000"), 3, false));
            this.setFont(new Font("Comic Sans", Font.ITALIC, 15));
            this.setHorizontalTextPosition(JButton.CENTER);
            this.setVerticalTextPosition(JButton.CENTER);
            this.setFocusable(false);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private static class ChoicePane extends JPanel { // reusable panel for ComboBox
        private final JComboBox<String> comboBox;
        private ChoicePane(int y, String[] options, Color background, boolean visible) {
            this.setBounds(50, y, 150, 50);
            this.comboBox = new JComboBox<>(options);
            comboBox.setFont(new Font("Comic Sans", Font.ITALIC, 15));
            comboBox.setBackground(background);
            comboBox.setForeground(getForeGroundColor(background));
            comboBox.setFocusable(false);
            comboBox.setOpaque(false);
            this.add(comboBox);
            this.setVisible(visible);
            this.setOpaque(false);
            this.setFocusable(false);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}
