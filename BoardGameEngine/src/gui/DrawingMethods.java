package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.BoardGameEngineMain;

public interface DrawingMethods {
	public default int round(double d) {
		return (int) Math.round(d);
	}

	public default void drawRect(Graphics2D g, double x, double y, double width, double height, Color color) {
		g.setColor(color);
		g.drawRect(round(x), round(y), round(width), round(height));
	}

	public default void fillRect(Graphics2D g, double x, double y, double width, double height, Color color) {
		g.setColor(color);
		g.fillRect(round(x), round(y), round(width), round(height));
	}

	public default void drawCenteredString(Graphics2D g, String text, double x, double y) {
		drawCenteredString(g, BoardGameEngineMain.DEFAULT_FONT, text, x, y);
	}

	public default void drawCenteredString(Graphics2D g, Font font, String text, double x, double y) {
		g.setFont(font);
		Rectangle glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getPixelBounds(null, 0, 0);
		double width = glyphVector.getWidth();
		double height = glyphVector.getHeight();
		g.drawString(text, round(x - width / 2), round(y + height / 2));
	}

	public default void drawCenteredYString(Graphics2D g, String text, double x, double y) {
		Rectangle glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getPixelBounds(null, 0, 0);
		double height = glyphVector.getHeight();
		g.drawString(text, round(x), round(y + height / 2));
	}

	public default void drawCircle(Graphics2D g, double x, double y, double radius) {
		double height = 2 * radius;
		g.drawOval(round(x - radius), round(y - radius), round(height), round(height));
	}

	public default void fillCircle(Graphics2D g, double x, double y, double radius) {
		double height = 2 * radius;
		g.fillOval(round(x - radius), round(y - radius), round(height), round(height));
	}

	public static int roundS(double d) {
		return (int) Math.round(d);
	}
}
