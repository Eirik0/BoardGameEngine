package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

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
		FontMetrics metrics = g.getFontMetrics();
		double height = metrics.getHeight();
		double width = metrics.stringWidth(text);
		g.drawString(text, round(x - width / 2), round(y + height / 3));
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
