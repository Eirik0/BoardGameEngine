package gui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public interface Drawable {
	public void drawOn(Graphics2D graphics);

	public default int round(Double d) {
		return (int) Math.round(d);
	}

	public default void drawCenteredString(Graphics2D g, String text, double x, double y) {
		g.setFont(g.getFont().deriveFont(44f));
		FontMetrics metrics = g.getFontMetrics();
		double height = metrics.getHeight();
		double width = metrics.stringWidth(text);
		g.drawString(text, round(x - width / 2), round(y + height / 3));
	}
}
