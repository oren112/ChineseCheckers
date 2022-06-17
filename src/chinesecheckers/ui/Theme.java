package chinesecheckers.ui;

import chinesecheckers.util.Piece;

import javax.swing.ImageIcon;

/**
 * Represents a Theme object that holds a set of reusable UI elements.
 */
public interface Theme {
    /**
     * Gets an icon showing the {@link Piece}.
     * @param piece the piece to get its icon
     * @return the icon associated with the {@code piece}
     */
    ImageIcon getImageIcon(Piece piece);

    /**
     * Gets an icon showing a marked position.
     * @return the icon showing a marked position
     */
    ImageIcon getMarkedPositionImageIcon();

    /**
     * Gets a small icon showing the {@link Piece}.
     * @param piece the piece to get its mini icon
     * @return the mini icon
     */
    ImageIcon getMiniImageIcon(Piece piece);
}
