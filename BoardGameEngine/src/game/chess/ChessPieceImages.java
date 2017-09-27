package game.chess;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

public class ChessPieceImages implements ChessConstants {
	public final BufferedImage whitePawn;
	public final BufferedImage whiteKnight;
	public final BufferedImage whiteBishop;
	public final BufferedImage whiteRook;
	public final BufferedImage whiteQueen;
	public final BufferedImage whiteKing;
	public final BufferedImage blackPawn;
	public final BufferedImage blackKnight;
	public final BufferedImage blackBishop;
	public final BufferedImage blackRook;
	public final BufferedImage blackQueen;
	public final BufferedImage blackKing;

	private static ChessPieceImages instance;

	public static synchronized ChessPieceImages getInstance() {
		if (instance == null) {
			instance = new ChessPieceImages();
		}
		return instance;
	}

	private ChessPieceImages() {
		whitePawn = loadImage("pawn16", true);
		whiteKnight = loadImage("knight16", true);
		whiteBishop = loadImage("bishop16", true);
		whiteRook = loadImage("rook16", true);
		whiteQueen = loadImage("queen16", true);
		whiteKing = loadImage("king16", true);
		blackPawn = loadImage("pawn16", false);
		blackKnight = loadImage("knight16", false);
		blackBishop = loadImage("bishop16", false);
		blackRook = loadImage("rook16", false);
		blackQueen = loadImage("queen16", false);
		blackKing = loadImage("king16", false);
	}

	private static BufferedImage loadImage(String name, boolean white) {
		BufferedImage image;
		try {
			image = ImageIO.read(ChessPieceImages.class.getResource("/game/chess/" + name + ".png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		BufferedImage pieceImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D pieceGraphics = pieceImage.createGraphics();
		// Clear background
		Composite composite = pieceGraphics.getComposite();
		pieceGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		pieceGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		pieceGraphics.setComposite(composite);
		// Copy
		Color pieceColor = white ? LIGHT_PIECE_COLOR : DARK_PIECE_COLOR;
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				int rgb = image.getRGB(x, y);
				if (rgb != -1) {
					pieceImage.setRGB(x, y, pieceColor.getRGB());
				}
			}
		}

		return pieceImage;
	}

	public BufferedImage getPieceImage(int piece) {
		switch (piece) {
		case BLACK_KING:
			return blackKing;
		case BLACK_QUEEN:
			return blackQueen;
		case BLACK_ROOK:
			return blackRook;
		case BLACK_BISHOP:
			return blackBishop;
		case BLACK_KNIGHT:
			return blackKnight;
		case BLACK_PAWN:
			return blackPawn;
		case UNPLAYED:
			return null;
		case WHITE_PAWN:
			return whitePawn;
		case WHITE_KNIGHT:
			return whiteKnight;
		case WHITE_BISHOP:
			return whiteBishop;
		case WHITE_ROOK:
			return whiteRook;
		case WHITE_QUEEN:
			return whiteQueen;
		case WHITE_KING:
			return whiteKing;
		default:
			throw new UnsupportedOperationException("Unknown piece: " + piece);
		}
	}
}
