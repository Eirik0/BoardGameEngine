package bge.game.photosynthesis;

import java.awt.image.BufferedImage;

import bge.main.PieceImages;
import gt.component.JavaGameImage;
import gt.gameentity.IGameImage;

public class PhotosynthesisPieceImages {
    public final JavaGameImage[] seeds = new JavaGameImage[4];
    public final JavaGameImage[] treesSmall = new JavaGameImage[4];
    public final JavaGameImage[] treesMedium = new JavaGameImage[4];
    public final JavaGameImage[] treesLarge = new JavaGameImage[4];

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

    private static void loadImages(String name, JavaGameImage[] images) {
        BufferedImage image = PieceImages.loadImage("photosynthesis", name);
        for (int i = 0; i < 4; ++i) {
            images[i] = PieceImages.toJavaGameImage(image, PhotosynthesisGameRenderer.PLAYER_COLORS[i]);
        }
    }

    public IGameImage getPieceImage(int treeLevel, int player) {
        switch (treeLevel) {
        case 0:
            return seeds[player];
        case 1:
            return treesSmall[player];
        case 2:
            return treesMedium[player];
        case 3:
            return treesLarge[player];
        default:
            throw new IllegalStateException("Unknown treeLevel: " + treeLevel);
        }
    }
}
