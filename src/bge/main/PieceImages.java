package bge.main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import bge.game.photosynthesis.PhotosynthesisPieceImages;
import gt.component.JavaGameImage;

public class PieceImages {
    public static BufferedImage loadImage(String gameName, String imageName) {
        try {
            return ImageIO.read(PhotosynthesisPieceImages.class.getResource("/bge/image/game/" + gameName + "/" + imageName + ".png"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static JavaGameImage toJavaGameImage(BufferedImage image, Color color) {
        JavaGameImage javaGameImage = new JavaGameImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D pieceGraphics = javaGameImage.getGraphics().getGraphics();
        BufferedImage pieceImage = javaGameImage.getImage();
        // Clear background
        Composite composite = pieceGraphics.getComposite();
        pieceGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        pieceGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        pieceGraphics.setComposite(composite);
        // Copy
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int rgb = image.getRGB(x, y);
                if (rgb != -1) {
                    pieceImage.setRGB(x, y, color.getRGB());
                }
            }
        }

        return javaGameImage;
    }
}
