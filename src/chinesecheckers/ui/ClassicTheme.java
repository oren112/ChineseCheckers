package chinesecheckers.ui;

import chinesecheckers.util.Piece;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A Theme implementation with classic board and piece images.
 */
public class ClassicTheme implements Theme {

    private ImageIcon whitePiece;
    private ImageIcon blackPiece;
    private ImageIcon bluePiece;
    private ImageIcon yellowPiece;
    private ImageIcon greenPiece;
    private ImageIcon redPiece;
    private ImageIcon emptyPiece;
    private ImageIcon miniWhitePiece;
    private ImageIcon miniBlackPiece;
    private ImageIcon miniBluePiece;
    private ImageIcon miniYellowPiece;
    private ImageIcon miniGreenPiece;
    private ImageIcon miniRedPiece;
    private ImageIcon miniEmptyPiece;
    private ImageIcon markedPosition;

    /**
     * Creates an object containing classic images for the game.
     */
    public ClassicTheme() {
        try {
            BufferedImage whiteImg = ImageIO.read(new File("resources/images/white_piece.png"));
            BufferedImage blackImg = ImageIO.read(new File("resources/images/black_piece.png"));
            BufferedImage blueImg = ImageIO.read(new File("resources/images/blue_piece.png"));
            BufferedImage yellowImg = ImageIO.read(new File("resources/images/yellow_piece.png"));
            BufferedImage greenImg = ImageIO.read(new File("resources/images/green_piece.png"));
            BufferedImage redImg = ImageIO.read(new File("resources/images/red_piece.png"));
            BufferedImage emptyImg = ImageIO.read(new File("resources/images/empty_position.png"));
            BufferedImage markedPositionImg = ImageIO.read(new File("resources/images/marked_position.png"));
            Image whiteScaledImg = whiteImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image blackScaledImg = blackImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image blueScaledImg = blueImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image yellowScaledImg = yellowImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image greenScaledImg = greenImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image redScaledImg = redImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image emptyScaledImg = emptyImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image markedPositionScaledImg = markedPositionImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            Image miniWhiteScaledImg = whiteImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniBlackScaledImg = blackImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniBlueScaledImg = blueImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniYellowScaledImg = yellowImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniGreenScaledImg = greenImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniRedScaledImg = redImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            Image miniEmptyScaledImg = emptyImg.getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            this.whitePiece = new ImageIcon(whiteScaledImg);
            this.blackPiece = new ImageIcon(blackScaledImg);
            this.bluePiece = new ImageIcon(blueScaledImg);
            this.yellowPiece = new ImageIcon(yellowScaledImg);
            this.greenPiece = new ImageIcon(greenScaledImg);
            this.redPiece = new ImageIcon(redScaledImg);
            this.emptyPiece = new ImageIcon(emptyScaledImg);
            this.markedPosition = new ImageIcon(markedPositionScaledImg);
            this.miniWhitePiece = new ImageIcon(miniWhiteScaledImg);
            this.miniBlackPiece = new ImageIcon(miniBlackScaledImg);
            this.miniBluePiece = new ImageIcon(miniBlueScaledImg);
            this.miniYellowPiece = new ImageIcon(miniYellowScaledImg);
            this.miniGreenPiece = new ImageIcon(miniGreenScaledImg);
            this.miniRedPiece = new ImageIcon(miniRedScaledImg);
            this.miniEmptyPiece = new ImageIcon(miniEmptyScaledImg);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Gets a marked position icon
     * @return a marked position {@link ImageIcon}
     */
    public ImageIcon getMarkedPositionImageIcon() {
        return markedPosition;
    }

    /**
     * Gets a classic piece icon.
     * @param piece the piece to get its icon
     * @return the piece icon
     */
    @Override
    public ImageIcon getImageIcon(Piece piece) {
        switch (piece) {
            case WHITE:
                return whitePiece;
            case BLACK:
                return blackPiece;
            case YELLOW:
                return yellowPiece;
            case BLUE:
                return bluePiece;
            case GREEN:
                return greenPiece;
            case RED:
                return redPiece;
            default:
                return emptyPiece;
        }
    }

    /**
     * Gets a small classic piece icon.
     * @param piece the piece to get its mini icon
     * @return the mini piece icon
     */
    @Override
    public ImageIcon getMiniImageIcon(Piece piece) {
        switch (piece) {
            case WHITE:
                return miniWhitePiece;
            case BLACK:
                return miniBlackPiece;
            case YELLOW:
                return miniYellowPiece;
            case BLUE:
                return miniBluePiece;
            case GREEN:
                return miniGreenPiece;
            case RED:
                return miniRedPiece;
            default:
                return miniEmptyPiece;
        }
    }
}
