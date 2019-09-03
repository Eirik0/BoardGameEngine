package bge.game.photosynthesis;

import java.awt.Color;
import java.awt.image.BufferedImage;

import bge.main.PieceImages;
import gt.component.JavaGameImage;
import gt.gameentity.DrawingMethods;
import gt.gameentity.IGameImage;

public class PhotosynthesisPieceImages {
    public final JavaGameImage[][] seeds = new JavaGameImage[4][2];
    public final JavaGameImage[][] treesSmall = new JavaGameImage[4][2];
    public final JavaGameImage[][] treesMedium = new JavaGameImage[4][2];
    public final JavaGameImage[][] treesLarge = new JavaGameImage[4][2];

    private static PhotosynthesisPieceImages instance;

    public static synchronized PhotosynthesisPieceImages getInstance() {
        if (instance == null) {
            instance = new PhotosynthesisPieceImages();
        }
        return instance;
    }

    private PhotosynthesisPieceImages() {
        loadImages("seed16", seeds);
        loadImages("tree-small16", treesSmall);
        loadImages("tree-medium16", treesMedium);
        loadImages("tree-large16", treesLarge);
    }

    private static void loadImages(String name, JavaGameImage[][] images) {
        BufferedImage image = PieceImages.loadImage("photosynthesis", name);
        for (int i = 0; i < 4; ++i) {
            Color color = PhotosynthesisGameRenderer.PLAYER_COLORS[i];
            images[i][0] = PieceImages.toJavaGameImage(image, color);
            images[i][1] = PieceImages.toJavaGameImage(image, DrawingMethods.fadeToColor(color, Color.BLACK, 0.75));
        }
    }

    public IGameImage getPieceImage(int treeLevel, int player, boolean shadow) {
        int shadowIndex = shadow ? 1 : 0;
        switch (treeLevel) {
        case 0:
            return seeds[player][shadowIndex];
        case 1:
            return treesSmall[player][shadowIndex];
        case 2:
            return treesMedium[player][shadowIndex];
        case 3:
            return treesLarge[player][shadowIndex];
        default:
            throw new IllegalStateException("Unknown treeLevel: " + treeLevel);
        }
    }
}
